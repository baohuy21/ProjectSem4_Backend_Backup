package com.shopee.shopeecareer.Service;
import com.shopee.shopeecareer.DTO.ApplicationDTO;
import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.Interviews;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private JavaMailSenderImpl mailSender;

    public void sendAccountActivationEmail(String toEmail, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi HTML email
        javaMailSender.send(mimeMessage);
    }

    // Phương thức mới: Gửi email reset mật khẩu
    public void sendPasswordResetEmail(String to, String subject, String newPassword, String name) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String content = String.format(
                "<h3>Password Reset Request</h3>" +
                        "<p>Dear %s,</p>" +
                        "<p>Your password has been reset successfully. Below is your new password:</p>" +
                        "<p><strong>%s</strong></p>" +
                        "<p>Please log in using this password and change it as soon as possible for security reasons.</p>" +
                        "<br><p>Thank you,<br>IT Support Team</p>",
                name, newPassword
        );
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public void sendApplicantEmail(Applications applications) throws MessagingException {
        String toEmail = applications.getEmail();
        String subject = "Applied Successfully - " + applications.getJobPostings().getJobTitle();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(applications.getApplicationDate());
        String content = String.format(
                "<h3>Congrats %s!</h3>" +
                        "<p>Your application for the job position of <strong>%s</strong> has been successfully submitted.</p>" +

                        "<p>Application details:</p>" +
                        "<ul>" +
                        "<li>Application Date: %s</li>" +
                        "<li>Position: %s</li>" +
                        "<li>Status: %s</li>" +
                        "</ul>" +
                        "<p>Thank you for applying to Shopee Career!</p>",
                applications.getFirstName(),
                applications.getJobPostings().getJobTitle(),
                formattedDate,
                applications.getJobPostings().getJobTitle(),
                applications.getApplicationStatus()
        );

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi email dưới dạng HTML
        javaMailSender.send(mimeMessage);
    }

    public void sendEmployerEmail(Applications applications) throws MessagingException {
        // Lấy thông tin của nhà tuyển dụng từ DTO hoặc Entity (ở đây lấy ví dụ trực tiếp từ DTO)
        String toEmail = applications.getJobPostings().getEmployers().getEmail();
        String subject = "Applicant " + applications.getFirstName() + " has applied - " + applications.getJobPostings().getJobTitle();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(applications.getApplicationDate());
        String content = String.format(
                "<h3>Notification: New Job Application</h3>" +
                        "<p>The candidate <strong>%s</strong> has submitted an application for the position of <strong>%s</strong></p>" +
                        "<p>Application details:</p>" +
                        "<ul>" +
                        "<li>Application Date: %s</li>" +
                        "<li>Position: %s</li>" +
                        "<li>Status: %s</li>" +
                        "<li>Candidate's Email: %s</li>" +
                        "<li>Phone Number: %s</li>" +
                        "</ul>" +
                        "<p>Please review and respond to the candidate.</p>",
                applications.getFirstName(),
//                applications.getLastName(),
                applications.getJobPostings().getJobTitle(),
                formattedDate,
                applications.getJobPostings().getJobTitle(),
                applications.getApplicationStatus(),
                applications.getEmail(),
                applications.getPhoneNumber()
        );

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi email dưới dạng HTML
        javaMailSender.send(mimeMessage);
    }

    public void sendInterviewScheduleEmail(Interviews interviews) throws MessagingException {
        String toEmail = interviews.getApplications().getEmail();
        String subject = "Interview Schedule - " + interviews.getApplications().getJobPostings().getJobTitle();
        String content = String.format(
                "<h3>Dear %s,</h3>" +
                "<p>We are pleased to inform you that you have been scheduled for an interview for the position <strong>%s</strong>.</p>" +
                "<p><strong>Interview Details:</strong></p>" +
                "<ul>" +
                "<li><strong>Date:</strong> %s</li>" +
                "<li><strong>Time:</strong> %s</li>" +
                "<li><strong>Location:</strong> %s</li>" +
                "</ul>" +
                "<p>Please ensure to arrive on time and prepare for the interview.</p>" +
                "<br><p>Best Regards,<br>Shopee Career Team</p>",
                interviews.getApplications().getFirstName(),
                interviews.getApplications().getJobPostings().getJobTitle(),
                interviews.getStartDate(),
                interviews.getTime(),
                interviews.getLocation()
        );
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi email dưới dạng HTML
        javaMailSender.send(mimeMessage);
    }

    public void sendUpdateInterviewScheduleEmail(Interviews interviews) throws MessagingException {
        String toEmail = interviews.getApplications().getEmail();
        String subject = "Updated Interview Schedule - " + interviews.getApplications().getJobPostings().getJobTitle();
        String content = String.format(
                "<h3>Dear %s,</h3>" +
                        "<p>We would like to inform you that the schedule for your interview for the position of <strong>%s</strong> has been updated.</p>" +
                        "<p><strong>Updated Interview Details:</strong></p>" +
                        "<ul>" +
                        "<li><strong>Date:</strong> %s</li>" +
                        "<li><strong>Time:</strong> %s</li>" +
                        "<li><strong>Location:</strong> %s</li>" +
                        "</ul>" +
                        "<p>We apologize for any inconvenience caused by this change. Please confirm your availability for the updated schedule by replying to this email at your earliest convenience.</p>" +
                        "<p>If you have any questions or need further assistance, feel free to contact us.</p>" +
                        "<br><p>Best Regards,<br>Shopee Career Team</p>",
                interviews.getApplications().getFirstName(),
                interviews.getApplications().getJobPostings().getJobTitle(),
                interviews.getStartDate(),
                interviews.getTime(),
                interviews.getLocation()
        );
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi email dưới dạng HTML
        javaMailSender.send(mimeMessage);
    }

    public void sendPassNotificationEmail(Applications applications) throws MessagingException {
        String toEmail = applications.getEmail();
        String subject = "Congratulations! You Have Passed the Interview - " + applications.getJobPostings().getJobTitle();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(applications.getApplicationDate());

        String content = String.format(
                "<h3>Dear %s,</h3>" +
                        "<p>Congratulations! You have successfully passed the interview for the position <strong>%s</strong>.</p>" +
                        "<p>Below are the details of your application:</p>" +
                        "<ul>" +
                        "<li>Application Date: %s</li>" +
                        "<li>Job Title: %s</li>" +
                        "<li>Status: <strong>Pass</strong></li>" +
                        "</ul>" +
                        "<p>We are excited to move forward with your application. You will receive further instructions soon.</p>" +
                        "<br><p>Best Regards,<br>Shopee Career Team</p>",
                applications.getFirstName(),
                applications.getJobPostings().getJobTitle(),
                formattedDate,
                applications.getJobPostings().getJobTitle()
        );

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi email dưới dạng HTML
        javaMailSender.send(mimeMessage);
    }

    public void sendFailNotificationEmail(Applications applications) throws MessagingException {
        String toEmail = applications.getEmail();
        String subject = "Unfortunately, You Have Not Passed the Interview - " + applications.getJobPostings().getJobTitle();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(applications.getApplicationDate());

        String content = String.format(
                "<h3>Dear %s,</h3>" +
                        "<p>Thank you for your interest in the position <strong>%s</strong>.</p>" +
                        "<p>Unfortunately, you were not selected for this role. We appreciate the time you spent in the interview process.</p>" +
                        "<p>Below are the details of your application:</p>" +
                        "<ul>" +
                        "<li>Application Date: %s</li>" +
                        "<li>Job Title: %s</li>" +
                        "<li>Status: <strong>Fail</strong></li>" +
                        "</ul>" +
                        "<p>We wish you the best of luck in your future job search, and encourage you to apply for other positions that may fit your qualifications in the future.</p>" +
                        "<br><p>Best Regards,<br>Shopee Career Team</p>",
                applications.getFirstName(),
                applications.getJobPostings().getJobTitle(),
                formattedDate,
                applications.getJobPostings().getJobTitle()
        );

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi email dưới dạng HTML
        javaMailSender.send(mimeMessage);
    }

    public void sendFailNotificationEmailRoundCV(Applications applications) throws MessagingException {
        String toEmail = applications.getEmail();
        String subject = "Unfortunately, Your CV Has Not Been Selected - " + applications.getJobPostings().getJobTitle();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(applications.getApplicationDate());

        String content = String.format(
                "<h3>Dear %s,</h3>" +
                        "<p>Thank you for your interest in the position <strong>%s</strong>.</p>" +
                        "<p>We have reviewed your CV and unfortunately, you were not selected to proceed further in the recruitment process for this role. We truly appreciate the time you took to submit your application.</p>" +
                        "<p>Below are the details of your application:</p>" +
                        "<ul>" +
                        "<li>Application Date: %s</li>" +
                        "<li>Job Title: %s</li>" +
                        "<li>Status: <strong>Fail</strong></li>" +
                        "</ul>" +
                        "<p>Although you were not selected for this position, we encourage you to apply for other positions at Shopee that may align better with your experience and qualifications in the future.</p>" +
                        "<br><p>Best Regards,<br>Shopee Career Team</p>",
                applications.getFirstName(),
                applications.getJobPostings().getJobTitle(),
                formattedDate,
                applications.getJobPostings().getJobTitle()
        );

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true); // true để gửi email dưới dạng HTML
        javaMailSender.send(mimeMessage);
    }
}
