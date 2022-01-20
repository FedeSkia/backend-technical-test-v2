package com.tui.proof.dto.response;

import com.tui.proof.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SearchResponse {

    private String name;
    private String lastName;
    private String telephone;
    private List<PilotesOrderDtoResponse> orders;
    private List<AddressResponse> customerAddreses;

}
