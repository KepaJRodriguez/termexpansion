package eu.ehri.termexpansion;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import eu.ehri.termexpansion.Workflows;



@Controller
@SessionAttributes
public class SearchController {
	private static final Logger logger = LoggerFactory
			.getLogger(SearchController.class);

	Workflows flow = new Workflows();

	/*
	 * To display the home page of the application
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! the client locale is " + locale.toString());

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}


	/*
	 * Expansion services Returns a list of all the terms resulting from the
	 * expansion in a JSON object. http://...../expand
	 */
	@RequestMapping(value = "/expandquery", method = RequestMethod.GET)
	public JSONObject expandQuery(@ModelAttribute("input") GetQuery input,
			BindingResult resultJSON, Model model) {

		System.out.println("Query: " + input.getQuery());
		JSONObject response = flow.workflowExp(input);

		List<ResultJSON> queryresults = new ArrayList<ResultJSON>();
		if (response != null) {

			queryresults.add(new ResultJSON(response));
			model.addAttribute("queryresults", queryresults);
			java.util.Collections.synchronizedList(queryresults);
			model.addAttribute("queryresults",
					java.util.Collections.synchronizedCollection(queryresults));
		}
		GetQuery q = new GetQuery();
		q.setQuery("");
		// return new ModelAndView("result", "command", q);
		return response;
	}

	@RequestMapping("/expand")
	public ModelAndView showQuery2() {
		System.out.println("EXPANSION ACTIVATED");
		return new ModelAndView("inputexpand", "command", new GetQuery());
	}


}