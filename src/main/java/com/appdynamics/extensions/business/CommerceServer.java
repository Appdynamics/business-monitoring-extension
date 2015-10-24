package com.appdynamics.extensions.business;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Created by kiran.gangadharappa on 9/17/15.
 */
public class CommerceServer extends AbstractHandler{

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/xml; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String text = getResponse();
        response.getWriter().println(text);

        baseRequest.setHandled(true);
    }

    public static void go() throws Exception {
//        Thread t = new Thread() {
//            public void run() {
//
//            }
//        }
        Server server = new Server(9999);
        server.setHandler(new CommerceServer());
        server.start();
//        server.join();
    }

    static final int[] values = {20, 80, 100};

    private String getResponse() {
        Integer ad = (int) (values[0] + Math.random() * values[0]);
        Integer np = (int) (values[1] + Math.random() * values[1]);
        Integer ta = (int) (values[2] + Math.random() * values[2]);
        Date date = new Date();
        int mins = date.getMinutes();
        if (mins < 5) {
            ad = ad * 2;
            np = np * 2;
            ta = ta / 2;
        }
        int hour = date.getHours();
        if (hour < 5) {
            ad = ad/4;
            np = np / 8;
            ta = ta * 3;
        }
        String ret = XmlTemplate.replace("$AD$", ad.toString());
        ret = ret.replace("$NP$", np.toString());
        ret = ret.replace("$TA$", ta.toString());
        ret = ret.replace("$TA1$", 5 + ta.toString());
        ret = ret.replace("$TA2$", 7 + ta.toString());
        ret = ret.replace("$TA3$", 3 + ta.toString());
        ret = ret.replace("$TA4$", 1 + ta.toString());
        ret = ret.replace("$TA5$", 2 + ta.toString());
        return ret;
    }

    private static final String XmlTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Metrics> \n"+
            "     <AuctionDuration name=\"Auction Duration\" description=\"specifies the duration for which auction that ended was active\">$AD$</AuctionDuration> \n" +
            "     <NumberOfParticipants name=\"Participants\" description=\"total number of participants both active as well as onlookers\">$NP$</NumberOfParticipants>\n"+
            "     <TransactionAmount name=\"Transaction Amount\" description=\"Sum of all auctions winning amounts aggregated in the last minute\">$TA$</TransactionAmount>\n" +
            "     <!-- This is a comment to about the paylod -->\n" +
            "     <ExtraInfo>\n" +
            "             <TransactionAmountEuro>$TA1$</TransactionAmountEuro>\n" +
            "             <TransactionAmountGBP>$TA2$</TransactionAmountGBP>\n" +
            "             <OverdueTransactionAmount>$TA3$</OverdueTransactionAmount>\n" +
            "             <CarvedTransactionAmount>$TA4$</CarvedTransactionAmount>\n" +
            "             <GuaranteedTransactionAmount>$TA5$</GuaranteedTransactionAmount>\n" +
            "      </ExtraInfo>\n" +
            "</Metrics>";
}
