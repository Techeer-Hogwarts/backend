package backend.techeerzip.global.config;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.role.entity.Role;
import backend.techeerzip.domain.role.repository.RoleRepository;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.entity.StackCategory;
import backend.techeerzip.domain.stack.repository.StackRepository;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final StackRepository stackRepository;
    private final CustomLogger logger;

    @PostConstruct
    @Transactional
    public void initData() {
        // Role 초기화
        if (roleRepository.count() == 0) {
            Role adminRole = roleRepository.save(new Role("admin"));
            Role mentorRole = roleRepository.save(new Role("mentor"));
            Role userRole = roleRepository.save(new Role("user"));

            // Role 계층 구조 설정
            mentorRole.setParent(adminRole);
            userRole.setParent(mentorRole);

            roleRepository.save(mentorRole);
            roleRepository.save(userRole);
        }

        // Stack 초기화
        if (stackRepository.count() == 0) {
            List<Stack> stacks =
                    List.of(
                            // Frontend
                            new Stack("React.js", StackCategory.FRONTEND),
                            new Stack("Vue.js", StackCategory.FRONTEND),
                            new Stack("Next.js", StackCategory.FRONTEND),
                            new Stack("SvelteKit", StackCategory.FRONTEND),
                            new Stack("Angular", StackCategory.FRONTEND),
                            new Stack("JavaScript/TypeScript", StackCategory.FRONTEND),
                            new Stack("Zustand", StackCategory.FRONTEND),
                            new Stack("Tailwind CSS", StackCategory.FRONTEND),
                            new Stack("Bootstrap", StackCategory.FRONTEND),
                            new Stack("Redux", StackCategory.FRONTEND),
                            new Stack("MobX", StackCategory.FRONTEND),
                            new Stack("Vuex", StackCategory.FRONTEND),
                            new Stack("Jest", StackCategory.FRONTEND),
                            new Stack("Mocha", StackCategory.FRONTEND),
                            new Stack("Cypress", StackCategory.FRONTEND),
                            new Stack("React Native", StackCategory.FRONTEND),
                            new Stack("Flutter", StackCategory.FRONTEND),
                            new Stack("NX", StackCategory.FRONTEND),
                            new Stack("Shadcn/ui", StackCategory.FRONTEND),
                            new Stack("Turborepo", StackCategory.FRONTEND),
                            new Stack("Lerna", StackCategory.FRONTEND),
                            new Stack("Chromatic", StackCategory.FRONTEND),
                            new Stack("PlayWright", StackCategory.FRONTEND),
                            new Stack("Storybook", StackCategory.FRONTEND),
                            new Stack("Vite", StackCategory.FRONTEND),
                            new Stack("Vitest", StackCategory.FRONTEND),
                            new Stack("React Testing Library", StackCategory.FRONTEND),

                            // Backend
                            new Stack("Django", StackCategory.BACKEND),
                            new Stack("Flask", StackCategory.BACKEND),
                            new Stack("Ruby on Rails", StackCategory.BACKEND),
                            new Stack("Spring Boot", StackCategory.BACKEND),
                            new Stack("Express.js", StackCategory.BACKEND),
                            new Stack("Laravel", StackCategory.BACKEND),
                            new Stack("Go Lang", StackCategory.BACKEND),
                            new Stack("Kafka", StackCategory.BACKEND),
                            new Stack("FastAPI", StackCategory.BACKEND),
                            new Stack("MSA", StackCategory.BACKEND),
                            new Stack("Java", StackCategory.BACKEND),
                            new Stack("Python", StackCategory.BACKEND),
                            new Stack("C/C++", StackCategory.BACKEND),
                            new Stack("C#", StackCategory.BACKEND),
                            new Stack("Ruby", StackCategory.BACKEND),
                            new Stack("Node.js", StackCategory.BACKEND),
                            new Stack("Apollo GraphQL", StackCategory.BACKEND),
                            new Stack("GraphQL", StackCategory.BACKEND),
                            new Stack("Nest.JS", StackCategory.BACKEND),
                            new Stack("Celery", StackCategory.BACKEND),

                            // DevOps
                            new Stack("S3/Cloud Storage", StackCategory.DEVOPS),
                            new Stack("Kubernetes", StackCategory.DEVOPS),
                            new Stack("Jenkins CI", StackCategory.DEVOPS),
                            new Stack("Github Actions", StackCategory.DEVOPS),
                            new Stack("Spinnaker", StackCategory.DEVOPS),
                            new Stack("Graphite", StackCategory.DEVOPS),
                            new Stack("Docker", StackCategory.DEVOPS),
                            new Stack("Ansible", StackCategory.DEVOPS),
                            new Stack("Terraform", StackCategory.DEVOPS),
                            new Stack("AWS", StackCategory.DEVOPS),
                            new Stack("GCP", StackCategory.DEVOPS),
                            new Stack("ELK Stack", StackCategory.DEVOPS),
                            new Stack("Elasticsearch", StackCategory.DEVOPS),
                            new Stack("Prometheus", StackCategory.DEVOPS),
                            new Stack("Grafana", StackCategory.DEVOPS),
                            new Stack("Nginx", StackCategory.DEVOPS),
                            new Stack("CDN (CloudFront)", StackCategory.DEVOPS),
                            new Stack("Traefik", StackCategory.DEVOPS),
                            new Stack("Docker Compose", StackCategory.DEVOPS),
                            new Stack("Docker Swarm", StackCategory.DEVOPS),
                            new Stack("RabbitMQ", StackCategory.DEVOPS),

                            // Database
                            new Stack("PostgreSQL", StackCategory.DATABASE),
                            new Stack("MySQL", StackCategory.DATABASE),
                            new Stack("MongoDB", StackCategory.DATABASE),
                            new Stack("Redis", StackCategory.DATABASE),

                            // Other
                            new Stack("AI/ML (Tensorflow, PyTorch)", StackCategory.OTHER),
                            new Stack("Selenium", StackCategory.OTHER),
                            new Stack("gRPC", StackCategory.OTHER),
                            new Stack("Figma", StackCategory.OTHER),
                            new Stack("Zeplin", StackCategory.OTHER),
                            new Stack("K6", StackCategory.OTHER),
                            new Stack("Locust", StackCategory.OTHER),
                            new Stack("JMeter", StackCategory.OTHER),
                            new Stack("Postman", StackCategory.OTHER),
                            new Stack("Insomnia", StackCategory.OTHER));

            stackRepository.saveAll(stacks);
        }
    }
}
