package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AppointmentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Appointment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentDetailsForDashBoardProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.TodayAppointmentProjection;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomAppointmentException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.exception.customException.CustomBadCredentialsException;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.AppointmentRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorMedicalCenterRoomScheduleRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.DoctorRepo;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final ModelMapper modelMapper;
    private final DoctorRepo doctorRepo;
    private final DoctorMedicalCenterRoomScheduleRepo doctorMedicalCenterRoomScheduleRepo;



    @Scheduled(cron = "* * * * * *")
    public void assignTimeToPendingAppointments() {
        List<Appointment> appointments = appointmentRepo.findByAppointmentDateAndStatus(getTodayDateOnly());

        Map<Long, List<Appointment>> appointmentsByDoctor = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getDoctorId));

        for (Map.Entry<Long, List<Appointment>> entry : appointmentsByDoctor.entrySet()) {

            long doctorId = entry.getKey();
            List<Appointment> doctorAppointments = entry.getValue();

            int patientCount = doctorRepo.findPatientCountByDoctorId(doctorId);

            DoctorMedicalCenterRoomSchedule schedule =
                    doctorMedicalCenterRoomScheduleRepo.findByDoctorIdAndDayOfWeekAndStatusActive(doctorId, getTodayDayName());

            List<String[]> slots = generateTimeSlots(schedule.getStartTime(), schedule.getEndTime(), patientCount);

            for (int i = 0; i < doctorAppointments.size(); i++) {
                Appointment app = doctorAppointments.get(i);
                if (i < slots.size()) {
                    String[] slot = slots.get(i);

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        Date startTime = sdf.parse(slot[0]);
                        Date endTime = sdf.parse(slot[1]);

                        Time currentTime = new Time(System.currentTimeMillis());
                        SimpleDateFormat sdf12Hour = new SimpleDateFormat("hh:mm:ss ");


//                        app.setAppointmentTime(new Time(startTime.getTime()));
//                        app.setAppointmentEndTime(new Time(endTime.getTime()));

                        if (sdf12Hour.format(currentTime).equals(sdf12Hour.format(app.getAppointmentEndTime()))) {

                            Appointment byAppointmentEndTimeAndDateAndStatus =
                                    appointmentRepo.findByAppointmentEndTimeAndDateAndStatus(
                                    app.getAppointmentEndTime(),
                                    app.getAppointmentDate()
                            );

                            if(byAppointmentEndTimeAndDateAndStatus.getAppointmentStatus().equals("Pending")) {

                                app.setAppointmentStatus("Closed");
                                //app.setAppointmentEndTime(new Time(sdf.parse("00:00").getTime()));
                                appointmentRepo.save(app);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }

    public List<String[]> generateTimeSlots(String startTime, String endTime, int patientCount) {
        List<String[]> slots = new ArrayList<>();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh.mm a"); // for input like "6.00 PM"
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");  // 24-hour format for output

            Date start = inputFormat.parse(startTime);
            Date end = inputFormat.parse(endTime);

            long totalMinutes = (end.getTime() - start.getTime()) / (60 * 1000);
            long slotMinutes = totalMinutes / patientCount;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            for (int i = 0; i < patientCount; i++) {
                Date slotStart = calendar.getTime();
                calendar.add(Calendar.MINUTE, (int) slotMinutes);
                Date slotEnd = calendar.getTime();

                slots.add(new String[]{
                        outputFormat.format(slotStart),
                        outputFormat.format(slotEnd)
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return slots;
    }



    public static Date getTodayDateOnly() {
        LocalDate today = LocalDate.now();
        return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String getTodayDayName() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }




/*    @Override
    public Appointment createAppointment(AppointmentDTO appointmentDTO, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }

        Appointment existingAppointment = appointmentRepo.findByIdAndStatus(appointmentDTO.getId());
        int patientCountByDoctorId = doctorRepo.findPatientCountByDoctorId(appointmentDTO.getDoctorId());
        DoctorMedicalCenterRoomSchedule byDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive =
                doctorMedicalCenterRoomScheduleRepo.
                        findByDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive(
                                appointmentDTO.getDoctorId(),
                                appointmentDTO.getMedicleCenterId(),
                                appointmentDTO.getDayOfWeek()
                        );

        if (existingAppointment == null) {
            int currentAppointmentCount = appointmentRepo.countByDoctorIdAndSheduleIdAndAppointmentDate(
                    appointmentDTO.getDoctorId(),
                    appointmentDTO.getSheduleId(),
                    appointmentDTO.getAppointmentDate()
            );

            if (currentAppointmentCount < patientCountByDoctorId) {
                int generatedChannelNumber = currentAppointmentCount + 1;


                // get schedule for the doctor
                DoctorMedicalCenterRoomSchedule schedule =
                        doctorMedicalCenterRoomScheduleRepo.findByDoctorIdAndDayOfWeekAndStatusActive(
                                appointmentDTO.getDoctorId(),
                                getDayNameFromDate(appointmentDTO.getAppointmentDate())
                        );



                // generate time slots
                List<String[]> timeSlots =
                        generateTimeSlots(schedule.getStartTime(), schedule.getEndTime(), patientCountByDoctorId);

                // get this appointment's slot
                String[] assignedSlot = timeSlots.get(currentAppointmentCount);
                Date appointmentTime = parseTimeOnly(assignedSlot[0]);
                Date appointmentEndTime = parseTimeOnly(assignedSlot[1]);// example: "09:00"

                // map and save
                Appointment appointment = modelMapper.map(appointmentDTO, Appointment.class);
                appointment.setAppointmentStatus("Pending");
                appointment.setChannelNumber(generatedChannelNumber);
                appointment.setAppointmentTime(new Time(appointmentTime.getTime()));
                appointment.setAppointmentEndTime(new Time(appointmentEndTime.getTime()));
                appointment.setSheduleId(byDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive.getId());
                appointment.setRoomId(byDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive.getChannelingRoomId());

                return appointmentRepo.save(appointment);
            } else {
                throw new CustomAppointmentException("Doctor is not available for this appointment date");
            }
        }

        throw new CustomAppointmentException("Appointment already exists with this ID or status is not pending");
    }*/


    @Override
    public Appointment createAppointment(AppointmentDTO appointmentDTO, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("You don't have permission to perform this action.");
        }

        // Find the doctor's schedule for the requested day
        DoctorMedicalCenterRoomSchedule schedule = doctorMedicalCenterRoomScheduleRepo
                .findByDoctorIdAndMedicalCenterIdAndDayOfWeekAndStatusActive(
                        appointmentDTO.getDoctorId(),
                        appointmentDTO.getMedicleCenterId(),
                        getDayNameFromDate(appointmentDTO.getAppointmentDate())
                );

        if (schedule == null) {
            throw new CustomAppointmentException("The doctor does not have a schedule for the selected day.");
        }

        // Get the total number of patients the doctor can see in this session
        int patientCountByDoctorId = doctorRepo.findPatientCountByDoctorId(appointmentDTO.getDoctorId());

        // Get the number of appointments already booked for this schedule on this date
        int currentAppointmentCount = appointmentRepo.countByDoctorIdAndSheduleIdAndAppointmentDate(
                appointmentDTO.getDoctorId(),
                schedule.getId(),
                appointmentDTO.getAppointmentDate()
        );

        // Check if the doctor's schedule is full
        if (currentAppointmentCount >= patientCountByDoctorId) {
            throw new CustomAppointmentException("The doctor's schedule is full for this day. Please choose another date.");
        }

        // Calculate the duration of each appointment slot
        long totalDurationMillis = parseTimeOnly(schedule.getEndTime()).getTime() - parseTimeOnly(schedule.getStartTime()).getTime();
        long slotDurationMillis = totalDurationMillis / patientCountByDoctorId;

        // Determine the start time of the new appointment
        long newAppointmentStartTimeMillis = parseTimeOnly(schedule.getStartTime()).getTime() + (currentAppointmentCount * slotDurationMillis);
        long newAppointmentEndTimeMillis = newAppointmentStartTimeMillis + slotDurationMillis;

        // Create and save the new appointment
        Appointment appointment = modelMapper.map(appointmentDTO, Appointment.class);
        appointment.setAppointmentStatus("Pending");
        appointment.setChannelNumber(currentAppointmentCount + 1);
        appointment.setAppointmentTime(new Time(newAppointmentStartTimeMillis));
        appointment.setAppointmentEndTime(new Time(newAppointmentEndTimeMillis));
        appointment.setSheduleId(schedule.getId());
        appointment.setRoomId(schedule.getChannelingRoomId());
        return appointmentRepo.save(appointment);
    }


    @Override
    public String getOnGoingChannelingNumber(long patientId, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        Appointment byPatientId = appointmentRepo.findByPatientId(patientId);
        if (!Objects.equals(byPatientId, null)) {
            String currentChannelNumberByMedicineCenterIdAndRoomId
                    = appointmentRepo.getCurrentChannelNumberByMedicineCenterIdAndRoomId(
                    byPatientId.getAppointmentDate(),
                    byPatientId.getMedicleCenterId(),
                    byPatientId.getRoomId()
            );
            return currentChannelNumberByMedicineCenterIdAndRoomId;
        }
        return "your appointment is closed";
    }

    @Override
    public List<AppointmentProjection> findAllAppointmentDetailsByPatientId(long patientId, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        return appointmentRepo.findAllAppointmentDetailsByPatientId(patientId);
    }

    @Override
    public Appointment deleteAppointment(long appointmentId, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        Appointment appointment = appointmentRepo.findByIdAndStatus(appointmentId);
        if (!Objects.equals(appointment,null)){
            appointmentRepo.delete(appointment);
            return appointment;
        }
        throw new CustomAppointmentException("Appointment does not exist");
    }

    @Override
    public List<AppointmentDetailsForDashBoardProjection> findAppointmentDetailsByPatientId(long patientId, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        return appointmentRepo.findAppointmentDetailsByPatientId(patientId);
    }

    @Override
    public int countByActiveDoctors(String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        return appointmentRepo.countPendingAppointments();
    }

    @Override
    public List<TodayAppointmentProjection> getTodayPendingAppointments(String type) {
        if (!type.equals("Admin")) {
            throw new CustomBadCredentialsException("dont have permission");
        }
        return appointmentRepo.getTodayPendingAppointments();
    }

    private Date parseTimeOnly(String timeStr) {
        List<String> possibleFormats = Arrays.asList(
                "HH:mm",       // 24-hour (e.g., 14:30)
                "hh:mm a",     // 12-hour with AM/PM (e.g., 09:00 PM)
                "h:mm a",      // single-digit hour with AM/PM (e.g., 9:00 PM)
                "h.mm a",      // e.g., 9.00 PM
                "hh.mm a"      // e.g., 09.00 PM
        );

        for (String format : possibleFormats) {
            try {
                return new SimpleDateFormat(format, Locale.ENGLISH).parse(timeStr.trim());
            } catch (ParseException ignored) {}
        }

        throw new RuntimeException("Time format is invalid. Expected formats: " + possibleFormats);
    }

    private String getDayNameFromDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH); // e.g., "Monday"
    }



}
