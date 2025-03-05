package com.documed.backend.schedules;

import com.documed.backend.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class FreeDay {
    private final int id;
    Date date;
    User user;
}
