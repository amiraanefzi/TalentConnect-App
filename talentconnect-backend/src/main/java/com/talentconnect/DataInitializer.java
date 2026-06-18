package com.talentconnect;

import com.talentconnect.entity.*;
import com.talentconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seed(
            UserRepository         userRepo,
            JobOfferRepository     jobRepo,
            ApplicationRepository  appRepo,
            ReferralRepository     referralRepo,
            NotificationRepository notifRepo) {

        return args -> {
            if (userRepo.count() > 0) {
                log.info("DataInitializer : DB déjà peuplée – seed ignoré");
                return;
            }
            log.info("DataInitializer : insertion des données initiales...");

            // ── 3 USERS ──────────────────────────────────────────────────

            User employee = userRepo.save(User.builder()
                    .employeeId("EMP-0001")
                    .firstName("Alice").lastName("Martin")
                    .email("employee@talentconnect.local")
                    .password(passwordEncoder.encode("password123"))
                    .role(User.Role.EMPLOYEE)
                    .department("Ingénierie").location("Paris")
                    .title("Développeuse Full-Stack")
                    .experienceYears(3)
                    .skills(List.of("Java", "Angular", "Spring Boot"))
                    .languages(List.of("Français", "Anglais"))
                    .build());

            User hr = userRepo.save(User.builder()
                    .employeeId("EMP-0002")
                    .firstName("Bernard").lastName("Dupont")
                    .email("rh@talentconnect.local")
                    .password(passwordEncoder.encode("password123"))
                    .role(User.Role.HR)
                    .department("Ressources Humaines").location("Lyon")
                    .title("Chargé de Recrutement")
                    .experienceYears(7)
                    .skills(List.of("Recrutement", "ATS", "Entretiens"))
                    .languages(List.of("Français"))
                    .build());

            User admin = userRepo.save(User.builder()
                    .employeeId("EMP-0003")
                    .firstName("Claire").lastName("Bernard")
                    .email("admin@talentconnect.local")
                    .password(passwordEncoder.encode("password123"))
                    .role(User.Role.ADMIN)
                    .department("Direction").location("Paris")
                    .title("Administratrice Système")
                    .experienceYears(10)
                    .skills(List.of("DevOps", "Gestion", "Sécurité"))
                    .languages(List.of("Français", "Anglais", "Espagnol"))
                    .build());

            // ── 5 OFFRES D'EMPLOI ────────────────────────────────────────

            JobOffer j1 = jobRepo.save(JobOffer.builder()
                    .title("Développeur Java Backend")
                    .department("Ingénierie").location("Paris")
                    .description("Nous recherchons un développeur Java senior pour renforcer notre équipe backend.")
                    .employmentType(JobOffer.EmploymentType.CDI)
                    .seniority(JobOffer.Seniority.SENIOR)
                    .status(JobOffer.Status.OPEN)
                    .requirements(List.of("Java 17+", "Spring Boot", "MySQL", "Docker"))
                    .tags(List.of("backend", "java", "microservices"))
                    .hiringManager("Bernard Dupont")
                    .recommendedScore(80)
                    .publishedAt(LocalDateTime.now().minusDays(5))
                    .closingAt(LocalDateTime.now().plusDays(25))
                    .build());

            JobOffer j2 = jobRepo.save(JobOffer.builder()
                    .title("Développeur Angular Frontend")
                    .department("Ingénierie").location("Lyon")
                    .description("Rejoignez notre équipe UI/UX pour construire des interfaces modernes.")
                    .employmentType(JobOffer.EmploymentType.CDI)
                    .seniority(JobOffer.Seniority.CONFIRME)
                    .status(JobOffer.Status.OPEN)
                    .requirements(List.of("Angular 16+", "TypeScript", "RxJS", "SCSS"))
                    .tags(List.of("frontend", "angular", "ux"))
                    .hiringManager("Bernard Dupont")
                    .recommendedScore(75)
                    .publishedAt(LocalDateTime.now().minusDays(3))
                    .closingAt(LocalDateTime.now().plusDays(27))
                    .build());

            JobOffer j3 = jobRepo.save(JobOffer.builder()
                    .title("Data Scientist")
                    .department("Data & IA").location("Paris")
                    .description("Analysez de grands volumes de données pour piloter la stratégie RH.")
                    .employmentType(JobOffer.EmploymentType.CDI)
                    .seniority(JobOffer.Seniority.SENIOR)
                    .status(JobOffer.Status.OPEN)
                    .requirements(List.of("Python", "Machine Learning", "SQL", "Spark"))
                    .tags(List.of("data", "ia", "python"))
                    .hiringManager("Claire Bernard")
                    .recommendedScore(90)
                    .publishedAt(LocalDateTime.now().minusDays(7))
                    .closingAt(LocalDateTime.now().plusDays(23))
                    .build());

            JobOffer j4 = jobRepo.save(JobOffer.builder()
                    .title("DevOps Engineer")
                    .department("Infrastructure").location("Télétravail")
                    .description("Automatisez et optimisez nos pipelines CI/CD sur Kubernetes.")
                    .employmentType(JobOffer.EmploymentType.CDI)
                    .seniority(JobOffer.Seniority.LEAD)
                    .status(JobOffer.Status.OPEN)
                    .requirements(List.of("Kubernetes", "Docker", "Jenkins", "Terraform"))
                    .tags(List.of("devops", "k8s", "cicd"))
                    .hiringManager("Claire Bernard")
                    .recommendedScore(85)
                    .publishedAt(LocalDateTime.now().minusDays(2))
                    .closingAt(LocalDateTime.now().plusDays(28))
                    .build());

            JobOffer j5 = jobRepo.save(JobOffer.builder()
                    .title("Chef de Projet IT")
                    .department("Management").location("Bordeaux")
                    .description("Pilotez des projets digitaux stratégiques en mode Agile/Scrum.")
                    .employmentType(JobOffer.EmploymentType.CDI)
                    .seniority(JobOffer.Seniority.LEAD)
                    .status(JobOffer.Status.OPEN)
                    .requirements(List.of("Gestion de projet", "Agile", "Scrum", "Communication"))
                    .tags(List.of("management", "agile", "projet"))
                    .hiringManager("Bernard Dupont")
                    .recommendedScore(70)
                    .publishedAt(LocalDateTime.now().minusDays(1))
                    .closingAt(LocalDateTime.now().plusDays(29))
                    .build());

            // ── 3 CANDIDATURES ────────────────────────────────────────────

            Application app1 = appRepo.save(Application.builder()
                    .job(j1).employee(employee)
                    .candidateName(employee.getFirstName() + " " + employee.getLastName())
                    .source(Application.Source.INTERNAL)
                    .status(Application.Status.REVIEW)
                    .score(82)
                    .notes("Profil très intéressant, à appeler en priorité.")
                    .build());

            Application app2 = appRepo.save(Application.builder()
                    .job(j2).employee(employee)
                    .candidateName(employee.getFirstName() + " " + employee.getLastName())
                    .source(Application.Source.INTERNAL)
                    .status(Application.Status.SUBMITTED)
                    .score(75)
                    .build());

            Application app3 = appRepo.save(Application.builder()
                    .job(j3).employee(null)
                    .candidateName("Thomas Leblanc")
                    .source(Application.Source.REFERRAL)
                    .status(Application.Status.INTERVIEW)
                    .score(91)
                    .notes("Candidat coopté, excellent parcours.")
                    .build());

            // ── 2 COOPTATIONS ─────────────────────────────────────────────

            referralRepo.save(Referral.builder()
                    .referrerEmployee(employee)
                    .candidateFullName("Sophie Renaud")
                    .candidateEmail("sophie.renaud@ext.com")
                    .candidatePhone("+33 6 12 34 56 78")
                    .linkedIn("https://linkedin.com/in/sophierenaud")
                    .targetJob(j4)
                    .skills(List.of("Docker", "Kubernetes", "CI/CD"))
                    .status(Referral.Status.SUBMITTED)
                    .build());

            referralRepo.save(Referral.builder()
                    .referrerEmployee(hr)
                    .candidateFullName("Marc Fontaine")
                    .candidateEmail("marc.fontaine@ext.com")
                    .candidatePhone("+33 7 98 76 54 32")
                    .targetJob(j5)
                    .skills(List.of("PMP", "Agile", "JIRA"))
                    .status(Referral.Status.DRAFT)
                    .build());

            // ── 4 NOTIFICATIONS ───────────────────────────────────────────

            notifRepo.save(Notification.builder()
                    .user(employee)
                    .type(Notification.NotifType.SUCCESS)
                    .title("Candidature envoyée !")
                    .message("Votre candidature pour \"Développeur Java Backend\" a bien été reçue.")
                    .deepLink("/applications")
                    .build());

            notifRepo.save(Notification.builder()
                    .user(employee)
                    .type(Notification.NotifType.INFO)
                    .title("Votre dossier est en cours d'examen")
                    .message("L'équipe RH examine votre candidature pour le poste Java Backend.")
                    .deepLink("/applications/" + app1.getId())
                    .build());

            notifRepo.save(Notification.builder()
                    .user(hr)
                    .type(Notification.NotifType.WARNING)
                    .title("Offre bientôt expirée")
                    .message("L'offre \"Data Scientist\" expire dans 7 jours.")
                    .deepLink("/jobs/" + j3.getId())
                    .build());

            notifRepo.save(Notification.builder()
                    .user(admin)
                    .type(Notification.NotifType.INFO)
                    .title("Nouveau utilisateur créé")
                    .message("L'utilisateur alice.martin a été créé avec succès.")
                    .deepLink("/users/" + employee.getId())
                    .build());

            log.info("DataInitializer : ✅ seed terminé – 3 users / 5 offres / 3 candidatures / 2 cooptations / 4 notifications");
        };
    }
}

