<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>My JSP 'index.jsp' starting page</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.3.2.min.js"></script>
</head>
<body>
<form id="searchForm">
    <h1>Weather</h1>
    city：
    <select name="switchCity" id="switchCity">
        <option value="">== 请选择 ==</option>
    </select>
    <div id="cityShow">
        <table>
            <tr>
                <td>City</td>
                <td>
                    <div id="location"></div>
                </td>
            </tr>
            <tr>
                <td>Updated time</td>
                <td>
                    <div id="last_update"></div>
                </td>
            </tr>
            <tr>
                <td>Weather</td>
                <td>
                    <div id="text"></div>
                </td>
            </tr>
            <tr>
                <td>Temperature</td>
                <td>
                    <div id="temperature"></div>
                </td>
            </tr>
        </table>
    <div>
</form>
</body>

<script>
    $(function () {
        $('#cityShow').hide();

        $('#switchCity').change(function () {
            var city = $('#switchCity')[0].value;

            if (city) {
                $('#cityShow').show();
                $.ajax({
                    type: "get",
                    url: "/Weather_war_exploded/weather/query",
                    dataType: "json",
                    data: {city: city},
                    success: function (data) {
                        if (data) {
                            $('#location').html(data.results[0].location.name);
                            $('#last_update').html(formatDateT(data.results[0].last_update));
                            $('#text').html(data.results[0].now.text);
                            $('#temperature').html(data.results[0].now.temperature);
                        }
                    },
                    error: function () {
                    }
                });
            } else {
                $('#location').html('');
                $('#last_update').html('');
                $('#text').html('');
                $('#temperature').html('');
                $('#cityShow').hide();
            }
        });

        $.ajax({
            type: "get",
            url: "/Weather_war_exploded/weather/initCity",
            data: {},
            dataType: "json",
            contentType: "application/json;charset=UTF-8",
            success: function (data) {
                if (data) {
                    var info = '';

                    var temp = eval(data);
                    for (var i = 0; i < temp.length; i++) {
                        info += "<option value='" + temp[i].code + "'>" + temp[i].name + "</option>";
                    }
                    $("#switchCity").append(info);
                }
            },
            error: function () {
            }
        });

    })

    function formatDateT(dataTime) {
        // var timestamp3 = 1551686227000;
        var timestamp = dataTime;
        var newDate = new Date(dataTime);
        // var newDate = new Date(dataTime + 8 * 3600 * 1000 );

        newDate.getTime(timestamp * 1000);
        // console.log(newDate.toDateString());//Mon Mar 11 2019
        // console.log(newDate.toGMTString()); //Mon, 11 Mar 2019 06:55:07 GMT
        // console.log(newDate.toISOString()); //2019-03-11T06:55:07.622Z
        return newDate.toGMTString();
    }
</script>
</html>