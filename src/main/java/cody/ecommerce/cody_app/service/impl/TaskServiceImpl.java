package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.repository.TaskRepository;
import cody.ecommerce.cody_app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


}
