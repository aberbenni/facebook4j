/*
 * Copyright 2012 Ryuji Yamashita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package facebook4j.internal.json;

import static facebook4j.internal.util.z_F4JInternalParseUtil.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import facebook4j.FacebookException;
import facebook4j.Insight;
import facebook4j.ResponseList;
import facebook4j.conf.Configuration;
import facebook4j.internal.http.HttpResponse;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
/*package*/ final class InsightJSONImpl implements Insight, java.io.Serializable {
    private static final long serialVersionUID = 5220288371199505577L;

    private String id;
    private String name;
    private String period;
    private List<Insight.Value> values;
    private String title;
    private String description;
    
    /*package*/InsightJSONImpl(HttpResponse res, Configuration conf) throws FacebookException {
        JSONObject json = res.asJSONObject();
        init(json);
        if (conf.isJSONStoreEnabled()) {
            DataObjectFactoryUtil.clearThreadLocalMap();
            DataObjectFactoryUtil.registerJSONObject(this, json);
        }
    }

    /*package*/InsightJSONImpl(JSONObject json) throws FacebookException {
        super();
        init(json);
    }

    private void init(JSONObject json) throws FacebookException {
        id = getRawString("id", json);
        name = getRawString("name", json);
        period = getRawString("period", json);
        values = createValueList(json);
        title = getRawString("title", json);
        description = getRawString("description", json);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPeriod() {
        return period;
    }

    public List<Insight.Value> getValues() {
        return values;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    
    /*package*/
    static ResponseList<Insight> createInsightList(HttpResponse res, Configuration conf) throws FacebookException {
        try {
            if (conf.isJSONStoreEnabled()) {
                DataObjectFactoryUtil.clearThreadLocalMap();
            }
            JSONObject json = res.asJSONObject();
            JSONArray list = json.getJSONArray("data");
            int size = list.length();
            ResponseList<Insight> insights = new ResponseListImpl<Insight>(size, json);
            for (int i = 0; i < size; i++) {
                Insight insight = new InsightJSONImpl(list.getJSONObject(i));
                insights.add(insight);
            }
            if (conf.isJSONStoreEnabled()) {
                DataObjectFactoryUtil.registerJSONObject(insights, json);
            }
            return insights;
        } catch (JSONException jsone) {
            throw new FacebookException(jsone);
        }
    }

    @Override
    public String toString() {
        return "InsightJSONImpl [id=" + id + ", name=" + name + ", period="
                + period + ", values=" + values + ", title=" + title
                + ", description=" + description + "]";
    }



    private final class ValueJSONImpl implements Insight.Value, java.io.Serializable {
        private static final long serialVersionUID = 8459191446733110167L;
        
        private Long value;
        private Date endTime;

        /*package*/ValueJSONImpl(JSONObject json) throws FacebookException {
            value = getLong("value", json);
            endTime = getFacebookDatetime("end_time", json);
        }

        public Long getValue() {
            return value;
        }

        public Date getEndTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return "ValueJSONImpl [value=" + value + ", endTime=" + endTime + "]";
        }
    }

    private List<Insight.Value> createValueList(JSONObject json) throws FacebookException {
        try {
            JSONArray list = json.getJSONArray("values");
            int size = list.length();
            List<Insight.Value> values = new ArrayList<Insight.Value>(size);
            for (int i = 0; i < size; i++) {
                Insight.Value value = new ValueJSONImpl(list.getJSONObject(i));
                values.add(value);
            }
            return values;
        } catch (JSONException jsone) {
            throw new FacebookException(jsone);
        }
    }
    
}