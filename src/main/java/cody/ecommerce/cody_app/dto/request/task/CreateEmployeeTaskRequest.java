package cody.ecommerce.cody_app.dto.request.task;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CreateEmployeeTaskRequest {
    private List<String> employeeId;
}

