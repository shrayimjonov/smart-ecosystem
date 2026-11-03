package uz.uzkassa.cts.dto.kapitallegal.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class KapitalLegalGetDocsRequest {
    private String sid;
    private String branch;
    private String account;
    private String date;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        KapitalLegalGetDocsRequest request = (KapitalLegalGetDocsRequest) o;

        if (!Objects.equals(branch, request.branch))
            return false;
        if (!Objects.equals(account, request.account))
            return false;
        return Objects.equals(date, request.date);
    }

    @Override
    public int hashCode() {
        int result = branch != null ? branch.hashCode() : 0;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KapitalLegalGetDocsRequest{" +
                "branch='" + branch + '\'' +
                ", account='" + account + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
