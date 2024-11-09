package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserCreateDto {
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email не соответствует формату")
    @NotBlank(message = "Поле email не должно быть пустым")
    private String email;
}
