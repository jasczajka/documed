package com.documed.backend.schedules;

import com.documed.backend.users.User;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FreeDay {
  private int id;
  Date date;
  User user;
}
