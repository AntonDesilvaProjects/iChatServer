package com.ichat.command;

import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Query extends AbstractStringCommand {
    private static final char QUERY_PARAM = 'q';
    private final String WOLFRAM_API_KEY = "XTLTEX-Q4LJK42KEX";
    private final String WOLFRAM_SHORT_ANSWER_ENDPOINT = "https://api.wolframalpha.com/v1/result";

    public Query() {
        super();
        Unirest.config().setObjectMapper(new JacksonObjectMapper());
    }

    @Override
    public String getDescription() {
        return "Query supports natural language/free-form searching of almost any kind of computational or knowledge-based information. For example, you can query things like <i>height of eiffel tower</i> or <i>derivative(sinx + 1)</i>";
    }

    @Override
    public String process(Map<String, String> paramArgMap) {
        if (MapUtils.isNotEmpty(paramArgMap) && paramArgMap.containsKey(HELP_PARAM)) {
            return help();
        }
        return generateHtmlView(getQueryResult(paramArgMap.get(QUERY_PARAM)));
    }

    @Override
    public Character getDefaultParameter() {
        return QUERY_PARAM;
    }

    @Override
    protected void initializeParams() {
        super.initializeParams();
        this.addParameter(QUERY_PARAM, buildHelpText(QUERY_PARAM, "query: search query","supports general free-form/natural language searches. Default."));
    }

    private String getQueryResult(String query) {
        if (StringUtils.isEmpty(query)) {
            return null;
        }
        String result = null;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("i", query);
        queryParams.put("appid", WOLFRAM_API_KEY);
        HttpResponse<String> response =  Unirest.get(WOLFRAM_SHORT_ANSWER_ENDPOINT).queryString(queryParams).asString();
        if (response != null && response.isSuccess()) {
            result = response.getBody();
        }
        return result;
    }

    private String generateHtmlView(String result) {
        if (StringUtils.isEmpty(result)) {
            result = "<span style=\"font-style: italic;\">No results. Rephrase your query and try again</span>";
        }
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<div class=\"system-message\">");
        htmlBuilder.append(result);
        htmlBuilder.append("</div>");
        return htmlBuilder.toString();
    }
}
