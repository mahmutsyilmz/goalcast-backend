package com.yilmaz.goalCast.dto.league;

import com.yilmaz.goalCast.model.Country;
import com.yilmaz.goalCast.model.LeagueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeagueCreateRequestDto {
  
    @NotBlank(message = "League name is required")
    @Size(max = 100, message = "League name must be at most 100 characters")
    private String name;

    private Country country;

    @NotNull(message = "League type is required")
    private LeagueType leagueType;
}
