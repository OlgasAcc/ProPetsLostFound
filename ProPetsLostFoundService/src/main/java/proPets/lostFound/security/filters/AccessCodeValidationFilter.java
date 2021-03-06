package proPets.lostFound.security.filters;

import java.io.IOException;
import java.security.AccessControlException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import proPets.lostFound.dao.postgreSQL.LostFoundPostgreSQLRepository;
import proPets.lostFound.model.accessCode.AccessCode;

@Component
@Order(10)

public class AccessCodeValidationFilter implements Filter {

	@Autowired
	LostFoundPostgreSQLRepository lostFoundJPARepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();

		if (checkStartPath(path)) {
			String accessCode = request.getParameter("accessCode");
			if (checkIfAccessCodeExists(accessCode)) {
				chain.doFilter(request, response);
				return;
			} else {
				throw new AccessControlException("Access code is not valid");
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean checkStartPath(String path) {
		boolean check = path.startsWith("/lostFound/lost/v1/all_matched") || path.startsWith("/lostFoundlost/v1/new_matched");
		check = check || path.startsWith("/lostFound/found/v1/all_matched") || path.startsWith("/lostFoundfound/v1/new_matched");
		return check;
	}

	private boolean checkIfAccessCodeExists(String accessCode) {
		AccessCode id = lostFoundJPARepository.findById(accessCode).orElse(null);
		if (id != null) {
			System.out.println("Code exists!");
			return true;
		} else {
			return false;
		}
	}
}
