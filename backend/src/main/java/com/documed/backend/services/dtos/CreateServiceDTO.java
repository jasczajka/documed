package com.documed.backend.services.dtos;

import com.documed.backend.services.model.ServiceType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateServiceDTO {

  @NotNull(message = "Nazwa jest wymagana") private String name;

  @NotNull(message = "Cena jest wymagana") @Positive(message = "Cena musi być większa od zera") private BigDecimal price;

  @NotNull(message = "Rodzaj usługi jest wymagany") private ServiceType type;

  @NotNull @Positive(message = "Czas trwania musi być większy od zera") private int estimatedTime;

  @NotNull @NotEmpty(message = "Musisz podać przynajmniej jedną specjalizację") private List<
          @NotNull(message = "ID specjalizacji nie może być puste") @Positive(message = "ID specjalizacji musi być większe od zera") Integer>
      specializationIds;
}
