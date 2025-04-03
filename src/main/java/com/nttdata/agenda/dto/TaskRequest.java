package com.nttdata.agenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TaskRequest {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Long user_id;
}
