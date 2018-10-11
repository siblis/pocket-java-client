package apiss;

public enum Authority {
    ADMINISTRATOR, CUSTOMER, NEW_CUSTOMER;

    String getAuthority() {
        return toString();
    }
}