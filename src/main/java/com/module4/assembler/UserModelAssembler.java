package com.module4.assembler;

import com.module4.controller.UserController;
import com.module4.dto.UserResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDTO, EntityModel<UserResponseDTO>> {

    @Override
    @NonNull
    public EntityModel<UserResponseDTO> toModel(@NonNull UserResponseDTO user) {
        return EntityModel.of(user,
                linkTo(UserController.class).slash(user.getId()).withSelfRel(),
                linkTo(UserController.class).slash(user.getId()).withRel("update"),
                linkTo(UserController.class).slash(user.getId()).withRel("patch"),
                linkTo(UserController.class).slash(user.getId()).withRel("delete")
        );
    }

    @NonNull
    public EntityModel<UserResponseDTO> toModelWithCreateLink(UserResponseDTO user) {
        EntityModel<UserResponseDTO> model = toModel(user);
        model.add(linkTo(UserController.class).withRel("create"));
        return model;
    }
}
