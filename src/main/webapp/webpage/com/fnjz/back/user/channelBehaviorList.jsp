<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">


    <div region="center" style="padding:0px;border:0px">
        <t:datagrid name="dataCenterList" title="用户信息" actionUrl="dataCenterController.do?datagrid"
                    idField="userId" fit="true">
            <%--<t:dgCol title="编号" field="id"></t:dgCol>--%>
            <t:dgCol title="用户id" field="userId" autocomplete="true"></t:dgCol>
            <t:dgCol title="用户昵称" field="userNickName" width="120" autocomplete="true"></t:dgCol>
            <t:dgCol title="注册时间" field="registerDate" formatter="yyyy-MM-dd hh:mm:ss" width="120" autocomplete="true"></t:dgCol>
            <t:dgCol title="下载渠道" field="downloadChannel" width="120"
                     replace="android_android,ios_ios" autocomplete="true"></t:dgCol>
            <t:dgCol title="记账总笔数" field="accountTotalTheNumber" width="120" autocomplete="true"></t:dgCol>
            <t:dgCol title="记账总天数" field="accountTotalDayNumber" width="120" autocomplete="true"></t:dgCol>
        </t:datagrid>

        <div style="padding: 3px; height: 70px" id="selectDiv">
            <div name="searchColums" style="float: left; padding-left: 15px;">
                <span>
                      <span style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 80px;text-align:right;"
                            title="用户id ">用户id: </span>
                      <input type="text" name="userId" style="width: 100px; height: 24px;">
                      <span style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 80px;text-align:right;"
                            title="终端系统">渠道: </span>
                      <select name="downloadChannel" id="downloadChannel" style="width: 80px">
                            <option value="all">全部</option>
                          <option value="xiaochengxu">小程序</option>
                            <c:forEach items="${channelList}" var="channel">
                                <option value=${channel}>${channel}</option>
                            </c:forEach>
                       </select>
                     <br/>
                     <br/>
                      <span style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 90px;text-align:right;"
                            title="注册时间 ">注册时间: </span>
                      <input type="text" name="registerDate_begin" style="width: 100px; height: 24px;">~
                      <input type="text" name="registerDate_end" style="width: 100px; height: 24px; margin-right: 20px;">
                 </span>
                <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dataCenterListsearch()" style="text-align: right;width: 670px">查询</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-putout" onclick="restart();">重置</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-putout" onclick="exportExcel();">导出excel</a>

            </div>
        </div>
    </div>
</div>


<script>
    $(document).ready(function () {
        $("input[name='registerDate_begin']").attr("class", "Wdate").attr("style", "height:20px;width:90px;").click(function () {
            WdatePicker({dateFmt: 'yyyy-MM-dd'});
        });
        $("input[name='registerDate_end']").attr("class", "Wdate").attr("style", "height:20px;width:90px;").click(function () {
            WdatePicker({dateFmt: 'yyyy-MM-dd'});
        });

    });
    function exportExcel() {
        /*var userId = $("*[name='userId']").val();
        var downloadChannel = $("*[name='downloadChannel']").val();
        var registerstartDate = $("*[name='registerDate_begin']").val();
        var registerendDate = $("*[name='registerDate_end']").val();*/

        var url = "dataCenterController.do?exportXls";
        JeecgExcelExport("dataCenterController.do?exportXls","dataCenterList");

        /*$.ajax({
            url: "dataCenterController.do?exportXls",
            data: {
                "userId": userId,
                "downloadChannel": downloadChannel,
                "registerstartDate": registerstartDate,
                "registerendDate": registerendDate,
                "page":page,
                "rows":rows
            },
            type: "Post",
            async: "true",
            dataType: "json",
            success: function (result) {
                console.log("666666666"+result)
                alert(result+"\"1111111111111111111\"");
            },
        })*/
    }

    function restart() {
        $("#selectDiv input").val("");
        $("#downloadChannel").val("");
    }

</script>