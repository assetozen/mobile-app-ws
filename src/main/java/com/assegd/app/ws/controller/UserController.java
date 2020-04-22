package com.assegd.app.ws.controller;

import com.assegd.app.ws.exceptions.UserServiceException;
import com.assegd.app.ws.service.AddressService;
import com.assegd.app.ws.service.UserService;
import com.assegd.app.ws.shared.dto.AddressDTO;
import com.assegd.app.ws.shared.dto.UserDto;
import com.assegd.app.ws.ui.model.request.UserDetailsRequestModel;
import com.assegd.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resources;
import javax.websocket.server.PathParam;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;
    @Autowired
    AddressService addressesService;

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);
        //changes from the newportit laptop
        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }


    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})

    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

        /* used to test exception handling*/
        /*if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        if (userDetails.getLastName().isEmpty()) throw new NullPointerException("The object is null");*/

        // Copies object content using BeanUtils
        //UserDto userDto = new UserDto(); BeanUtils.copyProperties(userDetails, userDto);
        //Copies object content using ModelMapper
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdDto = userService.createUser(userDto);
        //BeanUtils.copyProperties(createdDto, returnValue);
        UserRest returnValue = modelMapper.map(createdDto, UserRest.class);
        return returnValue;
    }

    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;

    }

    //http://localhost:8080/mobile-app-ws/users/adfsdkuser-id/addresses
    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> addressesListRestModel = new ArrayList<>();
        List<AddressDTO> addressesDTO = addressesService.getAddresses(id);

        if (addressesDTO != null && !addressesDTO.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            addressesListRestModel = new ModelMapper().map(addressesDTO, listType);

            for (AddressesRest addressRestModel: addressesListRestModel) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddresses(id)).withSelfRel();
                Link userLink = linkTo(UserController.class).slash(id).withRel("user");
                Link userLink_2 = linkTo(methodOn(UserController.class).getUser(id)).withRel("user_2");
                addressRestModel.add(addressLink);
                addressRestModel.add(userLink);
                addressRestModel.add(userLink_2);
            }
        }
        return new CollectionModel<>(addressesListRestModel);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDTO addressDTO = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();
        //hard coding the values addresses which are not variable in the uri
        //Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash("addressId").withSelfRel();
        //Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
        //Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");

        //Using MethodOn to minimize hardcoding "addresses" of addressLink
        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId,addressId)).withSelfRel();
        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

        AddressesRest addressesRest = modelMapper.map(addressDTO, AddressesRest.class);

        addressesRest.add(addressLink);
        addressesRest.add(userLink);
        addressesRest.add(addressesLink);

        return new EntityModel<>(addressesRest);
    }
}
