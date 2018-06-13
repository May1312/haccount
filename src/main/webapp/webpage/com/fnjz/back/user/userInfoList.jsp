<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">


    <div region="center" style="padding:0px;border:0px">
        <t:datagrid name="userInfoList" title="用户信息" actionUrl="userInfoController.do?datagrid" idField="id" fit="true">
            <t:dgCol title="编号" field="id"></t:dgCol>
            <t:dgCol title="昵称" field="nickName" width="120"></t:dgCol>
            <t:dgCol title="手机" field="mobile" width="120"></t:dgCol>
            <%--<t:dgCol title="邮箱" field="email" width="120"></t:dgCol>--%>
            <t:dgCol title="性别" field="sex" width="120"></t:dgCol>
            <t:dgCol title="出生年月日" field="birthday" formatter="yyyy-MM-dd" width="120"></t:dgCol>
            <%--<t:dgCol title="密码" field="password"   width="120"></t:dgCol>
            <t:dgCol title="手势密码" field="gesturePw"   width="120"></t:dgCol>
            <t:dgCol title="手势密码打开关闭状态 0关闭  1打开" field="gesturePwType"   width="120"></t:dgCol>
            <t:dgCol title="微信授权token" field="wechatAuth"   width="120"></t:dgCol>
            <t:dgCol title="微博授权token" field="weiboAuth"   width="120"></t:dgCol>
            <t:dgCol title="省份_id" field="provinceId"   width="120"></t:dgCol>
            <t:dgCol title="省份_value" field="provinceName"   width="120"></t:dgCol>
            <t:dgCol title="城市_id" field="cityId"   width="120"></t:dgCol>
            <t:dgCol title="城市_name" field="cityName"   width="120"></t:dgCol>
            <t:dgCol title="区县_id" field="districtId"   width="120"></t:dgCol>
            <t:dgCol title="区县_name" field="districtName"   width="120"></t:dgCol>
            <t:dgCol title="账户状态" field="status"   width="120"></t:dgCol>
            <t:dgCol title="用户类型,vip" field="userType"   width="120"></t:dgCol>--%>
            <t:dgCol title="用户所属行业" field="profession" width="120"></t:dgCol>
            <t:dgCol title="用户职位,基层/中层/高层" field="position" width="120"></t:dgCol>
            <t:dgCol title="年龄" field="age" width="120"></t:dgCol>
            <t:dgCol title="星座" field="constellation" width="120"></t:dgCol>
            <t:dgCol title="终端系统" field="mobileSystem" width="120"
                     replace="android_android,ios_ios"></t:dgCol>
            <%--<t:dgCol title="终端系统版本" field="mobileSystemVersion"   width="120"></t:dgCol>
            <t:dgCol title="终端厂商" field="mobileManufacturer"   width="120"></t:dgCol>
            <t:dgCol title="终端设备号" field="mobileDevice"   width="120"></t:dgCol>
            <t:dgCol title="ios_token标识" field="iosToken"   width="120"></t:dgCol>
            <t:dgCol title="登录ip" field="loginIp"   width="120"></t:dgCol>
            <t:dgCol title="用户头像" field="avatarUrl"   width="120"></t:dgCol>--%>
            <t:dgCol title="注册时间" field="registerDate" formatter="yyyy-MM-dd hh:mm:ss" width="120"
            ></t:dgCol>
            <t:dgCol title="操作" field="opt" width="100"></t:dgCol>
            <t:dgDelOpt title="删除" url="userInfoController.do?del&id={id}" urlclass="ace_button" urlfont="fa-trash-o"/>
            <%--<t:dgToolBar title="录入" icon="icon-add" url="userInfoController.do?addorupdate" funname="add"></t:dgToolBar>--%>
            <t:dgToolBar title="编辑" icon="icon-edit" url="userInfoController.do?addorupdate"
                         funname="update"></t:dgToolBar>
            <t:dgToolBar title="查看" icon="icon-search" url="userInfoController.do?addorupdate"
                         funname="detail"></t:dgToolBar>
        </t:datagrid>

        <div style="padding: 3px; height: 70px" id="selectDiv">

            <div name="searchColums" style="float: left; padding-left: 15px;">
        <span>
              <span style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 80px;text-align:right;"
                    title="用户id ">用户id: </span>
              <input type="text" name="id" style="width: 100px; height: 24px;">


              <span style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 80px;text-align:right;"
                    title="终端系统">终端系统: </span>
              <select name="mobileSystem" id="mobileSystem" style="width: 80px">
                  <option value="">请选择</option>
                  <option value="android">android</option>
                  <option value="ios">ios</option>
               </select>
             <br/>
             <br/>
              <span style="vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 90px;text-align:right;"
                    title="注册时间 ">注册时间: </span>
              <input type="text" name="registerDate_begin" style="width: 100px; height: 24px;">~
              <input type="text" name="registerDate_end" style="width: 100px; height: 24px; margin-right: 20px;">
         </span>
                <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="userInfoListsearch();"
                   style="text-align: right;width: 670px">查询</a>

                <%--<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-putout" onclick="restart();">重置</a>--%>
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

    function restart() {
        $("#selectDiv input").val("");
        $("#mobileSystem").val("");
    }
</script>