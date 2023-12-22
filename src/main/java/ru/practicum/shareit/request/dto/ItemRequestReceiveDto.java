package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestReceiveDto {
    @NotBlank(message = "ItemRequest's description should not be empty or null")
    private String description;
}
