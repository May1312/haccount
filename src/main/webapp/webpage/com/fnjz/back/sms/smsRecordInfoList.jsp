<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px">
        <t:datagrid name="smsRecordInfoList" title="发送记录" actionUrl="smsRecordInfoController.do?datagrid" idField="id"
                    fit="true" queryMode="group">
            <t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
            <t:dgCol title="创建发送记录id" field="smsrecordid" width="120"></t:dgCol>
            <t:dgCol title="发送内容" field="sendcontent" width="120"></t:dgCol>
            <t:dgCol title="发送状态" field="sendstate" width="120" query="true" replace="失败_fail,成功_success"></t:dgCol>
            <t:dgCol title="终端类型" field="terminaltype" width="120" query="true"
                     replace="android_android,ios_ios"></t:dgCol>
            <t:dgCol title="发送手机号" field="sendmobile" width="120" query="true"></t:dgCol>
            <t:dgCol title="发送模板id" field="sendtemplatecode" width="120"></t:dgCol>
            <t:dgCol title="发送时间" field="sendtime" formatter="yyyy-MM-dd hh:mm:ss" width="120" query="true"></t:dgCol>
            <t:dgCol title="返回时间" field="returntime" formatter="yyyy-MM-dd hh:mm:ss" width="120" query="true"></t:dgCol>
            <%--<t:dgCol title="删除标记" field="delFalg"   width="120"></t:dgCol>
            <t:dgCol title="删除时间" field="delDate" formatter="yyyy-MM-dd hh:mm:ss"  width="120"></t:dgCol>
            <t:dgCol title="备注说明" field="remark"   width="120"></t:dgCol>
            <t:dgCol title="备用字段" field="tag"   width="120"></t:dgCol>--%>
            <t:dgCol title="操作" field="opt" width="100"></t:dgCol>
            <t:dgDelOpt title="删除" url="smsRecordInfoController.do?del&id={id}" urlclass="ace_button"
                        urlfont="fa-trash-o"/>
            <t:dgToolBar title="录入" icon="icon-add" url="smsRecordInfoController.do?addorupdate"
                         funname="add"></t:dgToolBar>
            <t:dgToolBar title="编辑" icon="icon-edit" url="smsRecordInfoController.do?addorupdate"
                         funname="update"></t:dgToolBar>
            <t:dgToolBar title="查看" icon="icon-search" url="smsRecordInfoController.do?addorupdate"
                         funname="detail"></t:dgToolBar>
        </t:datagrid>
    </div>
</div>