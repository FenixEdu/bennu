package pt.ist.emailNotifier.presentationTier.servlets;

import javax.servlet.http.HttpServlet;

public class SendMailServlet extends HttpServlet {

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//	ServletOutputStream outputStream = response.getOutputStream();
//
//	String username = (String) request.getParameter("username");
//	String password = (String) request.getParameter("password");
//
//	if (!allowed(username, password)) {
//	    response.setContentType("text/plain");
//	    outputStream.print("NOT ALLOWED");
//	    outputStream.close();
//	}
//
//	String clientId = (String) request.getParameter("clientId");
//	String emailId = (String) request.getParameter("emailId");
//
//	String fromName = (String) request.getParameter("from");
//	String to = (String) request.getParameter("to");
//	String cc = (String) request.getParameter("cc");
//	String bcc = (String) request.getParameter("bcc");
//	String subject = (String) request.getParameter("subject");
//	String message = (String) request.getParameter("message");
//	String replyTo = (String) request.getParameter("replyTo");
//
//	Email email = createEmail(clientId, emailId, fromName, to, cc, bcc, replyTo, subject, message);
//
//	response.setContentType("text/plain");
//	outputStream.print(email.getIdInternal().toString());
//	outputStream.close();
//	return;
//    }
//
//    private boolean allowed(String username, String password) {
//	// please modify this
//	return username != null && username.equals(password);
//    }
//
//    @Service
//    private Email createEmail(String clientId, String emailId, String fromName, String to, String cc, String bcc, String replyTo,
//	    String subject, String message) {
//
//	ReceivedEmailRequest request = ReceivedEmailRequest.findRequest(clientId, emailId);
//
//	if (request != null) {
//	    return request.getEmail();
//	}
//
//	String[] ccAddress = cc.split(",");
//	String[] bccAddress = bcc.split(",");
//	String[] toAddress = to.split(",");
//	String[] replyToAddress = replyTo.split(",");
//
//	Email email = new Email(fromName, "noreply@ist.utl.pt", replyToAddress, Arrays.asList(toAddress), Arrays
//		.asList(ccAddress), Arrays.asList(bccAddress), subject, message);
//
//	new ReceivedEmailRequest(clientId, emailId, email);
//
//	return email;
//    }
}
