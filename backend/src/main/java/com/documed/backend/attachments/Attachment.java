package com.documed.backend.attachments;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.visits.Visit;
import lombok.Data;
import lombok.NonNull;

@Data
public class Attachment {
    private final int id;
    @NonNull
    private String url;
    private Visit visit;
    private AdditionalService additionalService;
}
