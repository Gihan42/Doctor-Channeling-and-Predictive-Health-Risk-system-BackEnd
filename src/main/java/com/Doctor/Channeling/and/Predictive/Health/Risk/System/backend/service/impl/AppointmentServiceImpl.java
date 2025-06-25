package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.service.impl;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.dto.AppointmentDTO;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.Appointment;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.DoctorMedicalCenterRoomSchedule;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.custom.AppointmentProjection;
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



    @Scheduled(cron = "* * * * * ?")
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
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);

            long totalMinutes = (end.getTime() - start.getTime()) / (60 * 1000);
            long slotMinutes = totalMinutes / patientCount;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            for (int i = 0; i < patientCount; i++) {
                Date slotStart = calendar.getTime();
                calendar.add(Calendar.MINUTE, (int) slotMinutes);
                Date slotEnd = calendar.getTime();

                slots.add(new String[]{sdf.format(slotStart), sdf.format(slotEnd)});
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




    @Override
    public Appointment createAppointment(AppointmentDTO appointmentDTO, String type) {
        if (!type.equals("Patient")) {
            throw new CustomBadCredentialsException("dont have permission");
        }

        Appointment existingAppointment = appointmentRepo.findByIdAndStatus(appointmentDTO.getId());
        int patientCountByDoctorId = doctorRepo.findPatientCountByDoctorId(appointmentDTO.getDoctorId());

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

                return appointmentRepo.save(appointment);
            } else {
                throw new CustomAppointmentException("Doctor is not available for this appointment date");
            }
        }

        throw new CustomAppointmentException("Appointment already exists with this ID or status is not pending");
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

    private Date parseTimeOnly(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getDayNameFromDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH); // e.g., "Monday"
    }



}
