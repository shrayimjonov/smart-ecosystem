package uz.uzkassa.smartpos.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpos.dto.eimzo.SignatureCertificateDto;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.EimzoClient;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignatureAuthenticationProvider implements AuthenticationProvider {
    private final EimzoClient eimzoClient;
    private final CompanyRepository companyRepository;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth instanceof SignatureAuthenticationToken) {
            final String sign = ((SignatureAuthenticationToken) auth).getSign();
            Pkcs7Dto pkcs7Dto = eimzoClient.backendAuth(sign);
            if (pkcs7Dto.isSuccess()) {
                SignatureCertificateDto certificateDto = pkcs7Dto.getSubjectCertificateInfo();
                final String tin = certificateDto.getSubjectName().getTinOrPinFl();
                log.debug("Authenticating TIN: {}", tin);
                Company customer = companyRepository.findOneByInn(tin).orElse(null);
                if (customer == null || customer.getOwner() == null) {
                    throw new EntityNotFound("Customer not registered");
                }
                CustomUser userDetails = createSpringSecurityUser(customer.getOwner());
                log.debug("Login with User: {}", userDetails);
                return new SignatureAuthenticationToken(userDetails, "", userDetails.getAuthorities(), "");
            } else {
                throw new AuthenticationServiceException("Cannot authenticate: " + pkcs7Dto.getMessage());
            }
        }
        throw new BadCredentialsException("External system authentication failed");
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(SignatureAuthenticationToken.class);
    }

    private CustomUser createSpringSecurityUser(User user) {
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new CustomUser(user.getLogin(),
                user.getPassword(),
                grantedAuthorities, user.getId(), user.getCompany().getId(), user.getBranch().getId(), user.getBranchGroupId());
    }
}
