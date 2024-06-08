package kimp.websocket;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketMessage {

    @JsonProperty("ticket")
    private String ticket;

    public TicketMessage(String ticket) {
        this.ticket = ticket;
    }

    public String getTicket() {
        return ticket;
    }

}
