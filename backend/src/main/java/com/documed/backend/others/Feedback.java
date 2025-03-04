package com.documed.backend.others;

import com.documed.backend.visits.Visit;
import lombok.Data;

@Data
public class Feedback {
    private final int id;
    private final int rating;
    private final String text;
    private final Visit visit;
}
