package uz.uzkassa.smartpos.web.rest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.SecurityConfiguration;
import uz.uzkassa.smartpos.dto.integration.erp.TerminalCashierErpCreateDto;
import uz.uzkassa.smartpos.dto.receipt.LastReceiptDto;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.integration.cto.CtoResultDTO;
import uz.uzkassa.smartpos.security.CustomAccessDeniedHandler;
import uz.uzkassa.smartpos.security.CustomAuthenticationEntryPoint;
import uz.uzkassa.smartpos.security.DomainUserDetailsService;
import uz.uzkassa.smartpos.security.SignatureAuthenticationProvider;
import uz.uzkassa.smartpos.security.jwt.JWTFilter;
import uz.uzkassa.smartpos.security.jwt.TokenProvider;
import uz.uzkassa.smartpos.service.DeviceService;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.ReceiptService;
import uz.uzkassa.smartpos.service.UserService;
import uz.uzkassa.smartpos.social.TelegramBotApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import({JacksonConfiguration.class, JWTFilter.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class, SecurityConfiguration.class})
@ActiveProfiles("test")
@WebMvcTest(value = ErpResource.class)
class ErpResourceTest {

    @MockBean
    DomainUserDetailsService domainUserDetailsService;
    @MockBean
    SignatureAuthenticationProvider signatureAuthenticationProvider;
    @MockBean
    TelegramBotApi telegramBotApi;
    @MockBean
    LocalizationService localizationService;
    @MockBean
    TokenProvider tokenProvider;
    @MockBean
    ReceiptService receiptService;
    @MockBean
    UserService userService;
    @MockBean
    DeviceService deviceService;


    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser
    void terminalCashier() throws Exception {
        String request = objectMapper.writeValueAsString(Instancio.create(TerminalCashierErpCreateDto.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/integration/erp/terminal/cashier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void removeDeviceFromBranch() throws Exception {
        String serialNumber = RandomStringUtils.randomAlphabetic(15);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/integration/erp/devices/{serialNumber}", serialNumber))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(deviceService, Mockito.times(1)).removeDeviceFromBranch(serialNumber);
    }

    @Test
    @WithMockUser
    void syncDevice() throws Exception {
        String request = objectMapper.writeValueAsString(Instancio.create(CtoResultDTO.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/integration/erp/devices/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void lastReceipt() throws Exception {
        LastReceiptDto dto = Instancio.create(LastReceiptDto.class);
        when(receiptService.getLastReceipt(anyString(), anyString(), any(PaymentType.class))).thenReturn(dto);
        String serialNumber = RandomStringUtils.randomAlphabetic(15);
        String tin = RandomStringUtils.randomAlphabetic(14);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/integration/erp/receipt/last")
                        .param("serialNumber", serialNumber)
                        .param("tin", tin)
                        .param("paymentType", "HUMO"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    LastReceiptDto response = objectMapper.readValue(contentAsString, LastReceiptDto.class);
                    assertEquals(response.getFiscalUrl(), dto.getFiscalUrl());
                    assertEquals(response.getReceiptDateTime(), dto.getReceiptDateTime());
                });
    }
}