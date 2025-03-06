package com.shopee.shopeecareer.UserController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shopee.shopeecareer.Entity.Education;
import com.shopee.shopeecareer.Entity.Experience;
import com.shopee.shopeecareer.Entity.ProfileUser;
import com.shopee.shopeecareer.Entity.Project;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.EducationRepo;
import com.shopee.shopeecareer.Repository.ExperienceRepo;
import com.shopee.shopeecareer.Repository.ProfileUserRepo;
import com.shopee.shopeecareer.Repository.ProjectRepo;
import com.shopee.shopeecareer.DTO.EducationDTO;
import com.shopee.shopeecareer.DTO.ExperienceDTO;
import com.shopee.shopeecareer.DTO.ProjectDTO;

@Service
public class UserService {
    @Autowired
    private ExperienceRepo experienceRepo;

    @Autowired
    private EducationRepo educationRepo;
    @Autowired
    private ProfileUserRepo profileUserRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Value("${config.upload-dir}")
    private String uploadDir;

    public List<Experience> getExperience(int id) {
        return experienceRepo.findExperiencesByUserId(id);
    }

    public List<Education> getEducation(int id) {
        return educationRepo.findEducationByApplicantId(id);
    }

    public Experience saveExperience(ExperienceDTO experienceDTO) {
        ProfileUser profileUser = profileUserRepo.findById(experienceDTO.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));
        Experience experience = new Experience();
        BeanUtils.copyProperties(experienceDTO, experience);
        experience.setProfileUser(profileUser);
        experienceRepo.save(experience);
        return experience;
    }

    public ProfileUser sendCV(uploadCVDTO uploadCVDTO) {
        ProfileUser profileUser = profileUserRepo.findByEmail(uploadCVDTO.getEmail());
        if (profileUser == null) {
            throw new BadRequestException("User not found");
        }
        MultipartFile file = uploadCVDTO.getFile();
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file uploaded");
        }

        if (file != null && !file.isEmpty()) {
            try {
                Path path = Paths.get(uploadDir + "/filecv");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String filename = file.getOriginalFilename();
                Path filePath = path.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                // application.setResumefile(filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new BadRequestException("No File upload");
        }

        return profileUser;
    }

    public Education saveEducation(EducationDTO educationDTO) {
        ProfileUser profileUser = profileUserRepo.findById(educationDTO.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));
        Education education = new Education();
        BeanUtils.copyProperties(educationDTO, education);
        education.setProfileUser(profileUser);
        educationRepo.save(education);
        return education;
    }

    public List<Project> getProject(int id) {
        return projectRepo.findProjectsByUserId(id);
    }

    public Project saveProject(ProjectDTO projectDTO) {
        ProfileUser profileUser = profileUserRepo.findById(projectDTO.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project);
        project.setProfileUser(profileUser);
        projectRepo.save(project);
        return project;
    }

}
