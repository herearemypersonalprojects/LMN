
package com.travelsoft.lastminute.catalog.partnercode;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.travelsoft.cameleo.cms.processor.messages.MessageRetriever;
import com.travelsoft.lastminute.catalog.util.Util;

/**
 * Servlet that writes a partner code matching given partnerId.
 * @author jerome.ruillier@travelsoft.fr
 */
public class FindPartnerCodeServlet extends HttpServlet {

	/** "fileName". */
	static final String FILE_NAME_INITPARAM = "fileName";

	/** "partnerId". */
	static final String PARTNER_ID_HTTPPARAM = "partnerId";

	private

	/** Message retriever in use (lazy loading). */
	MessageRetriever messageRetriever;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(
			final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {

		final String koReturn = "KO " +
"################################################################################" +
"################################################################################" +
"################################################################################" +
"################################################################################" +
"################################################################################" +
"################################################################################" +
"################################################################################" +
"################################################################################" +
"################################################################################";

		// prepare
		final String partnerId = req.getParameter(PARTNER_ID_HTTPPARAM);

		// execute
		final String partnerCode = this.findPartnerCode(partnerId);

		// render
		resp.getWriter().write(partnerCode == null ? koReturn : partnerCode);
	}

	/**
	 * Finds partner code matching partner id. Uses a property file.
	 * @param partnerId to look for
	 * @return matching partnerCode
	 */
	private String findPartnerCode(
			final String partnerId) {

		return this.getMessageRetriever().createMessage(
				partnerId, null);
	}

	/**
	 * Gets (and initializes) a MessageRetriever instance.
	 * @return a message retriever instance
	 */
	private MessageRetriever getMessageRetriever() {
		if (this.messageRetriever == null) {
			this.messageRetriever =
					new MessageRetriever(
							Util.getPageIdentifierInstance(),
							this.getInitParameter(FILE_NAME_INITPARAM));
		}
		return this.messageRetriever;
	}

}
