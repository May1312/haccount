<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户信息</title>
    <t:base type="jquery,easyui,tools,DatePicker"></t:base>
</head>
<body style="overflow-y: " scroll="yes">


起止时间<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})"
           style="width: 150px" id="beginDate" name="beginDate"
           value="${startDate}"/>~

<input class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})"
       style="width: 150px" id="endDate" name="endDate"
       value="${endDate}"/>


<button id="query" onclick="query()">查询</button>
<br><br>


总条数:<input value="${map.countTatalNumber}"><br>

<br><br>

性别属性: <input class="inputxt" value="${map.sexTotalNumber}"/><br>
男: <input class="inputxt" value="${map.manNumber}"/>
女: <input class="inputxt" value="${map.womanNumber}"/>
<br><br>

职位属性: <input class="inputxt" value="${map.positionTotalNumber}"/><br>
基层: <input class="inputxt" value="${map.lowPosition}"/>
中层: <input class="inputxt" value="${map.centrePosition}"/>
高层: <input class="inputxt" value="${map.highPosition}"/>
<br><br>

年龄属性: ${map.ageTotalNumber}<br>
10-20: <input class="inputxt" value="${map.oneToTwoNumber}"/>
20-30: <input class="inputxt" value="${map.twoToThreeNumber}"/>
30-40: <input class="inputxt" value="${map.threeToFourNumber}"/>
>40: <input class="inputxt" value="${map.afterFourNumber}"/>

<br><br>
地区属性: ${map.ProviceTotalNumber}<br>
<c:forEach items="${map.everyProviceList}" var="proviceList">${proviceList.province_name}: <input
        class="inputxt" value="${proviceList.ProviceCount}"/></c:forEach>

<br><br>


星座属性: ${map.constellationTotalCount}<br>
<c:forEach items="${map.everyConstellList}" var="constellList">
    ${constellList.constellation}: <input class="inputxt"
                                          value="${constellList.constellationCount}"/></c:forEach>


</body>


<script>

    function query() {
        var beginDate=$("#beginDate").val();
        var endDate=$("#endDate").val();
        if (beginDate!="" && endDate==""){
            alert("输入结束时间");
            return false;
        }
        if (beginDate=="" && endDate!=""){
            alert("输入开始时间");
            return false;
        }
        var i = window.location.href="dataCenterController.do?list&beginDate="+beginDate+"&endDate="+endDate;



        /*$.ajax({
            url: 'dataCenterController.do?list',
            data: {'beginDate': beginDate,'endDate': endDate},
            dataType: 'JSON',
            type: 'post',
            error: erryFunction,  //错误执行方法
            success: succFunction //成功执行方法
        });

        function erryFunction() {
            alert("error");
        }

        function succFunction(j) {
            alert(j.msg);
            window.location.reload();
        }*/
    }
</script>