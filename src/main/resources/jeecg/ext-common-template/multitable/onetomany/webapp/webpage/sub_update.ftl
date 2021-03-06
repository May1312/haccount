<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<#include "/ui/datatype.ftl"/>
<#include "/ui/dictInfo.ftl"/>
<#include "/ui/tag.ftl"/>
<#include "/ui/formControl.ftl"/>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${subsG['${key}'].ftlDescription}</title>
  <meta name="description" content="">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="online/template/ledefault/css/vendor.css">
  <link rel="stylesheet" href="online/template/ledefault/css/bootstrap-theme.css">
  <link rel="stylesheet" href="online/template/ledefault/css/bootstrap.css">
  <link rel="stylesheet" href="online/template/ledefault/css/app.css">
  
  <link rel="stylesheet" href="plug-in/Validform/css/metrole/style.css" type="text/css"/>
  <link rel="stylesheet" href="plug-in/Validform/css/metrole/tablefrom.css" type="text/css"/>
  
  <script type="text/javascript" src="plug-in/jquery/jquery-1.8.3.js"></script>
  <script type="text/javascript" src="plug-in/tools/dataformat.js"></script>
  <script type="text/javascript" src="plug-in/easyui/jquery.easyui.min.1.3.2.js"></script>
  <script type="text/javascript" src="plug-in/easyui/locale/zh-cn.js"></script>
  <script type="text/javascript" src="plug-in/tools/syUtil.js"></script>
  <script type="text/javascript" src="plug-in/My97DatePicker/WdatePicker.js"></script>
  <script type="text/javascript" src="plug-in/lhgDialog/lhgdialog.min.js"></script>
  <script type="text/javascript" src="plug-in/tools/curdtools_zh-cn.js"></script>
  <script type="text/javascript" src="plug-in/tools/easyuiextend.js"></script>
  <script type="text/javascript" src="plug-in/Validform/js/Validform_v5.3.1_min_zh-cn.js"></script>
  <script type="text/javascript" src="plug-in/Validform/js/Validform_Datatype_zh-cn.js"></script>
  <script type="text/javascript" src="plug-in/Validform/js/datatype_zh-cn.js"></script>
  <script type="text/javascript" src="plug-in/Validform/plugin/passwordStrength/passwordStrength-min.js"></script>
  <script type="text/javascript"  charset="utf-8" src="plug-in/ueditor/ueditor.config.js"></script>
  <script type="text/javascript"  charset="utf-8" src="plug-in/ueditor/ueditor.all.min.js"></script>
 
  <#if subsG['${key}'].cgFormHead.isTree=='Y'>  
  <link rel="stylesheet" href="plug-in/easyui/themes/metrole/easyui.css" type="text/css">
  <style type="text/css">
  	.combo_self{height: 26px !important;width:164px !important;padding-top:0px !important;}
  	.layout-header .btn {
	    margin:0px;
	   float: none !important;
	}
	.btn-default {
	    height: 35px;
	    line-height: 35px;
	    font-size:14px;
	}
  </style>
  <script type="text/javascript">
	$(function(){
		$(".combo").removeClass("combo").addClass("combo combo_self");
		$(".combo").each(function(){
			$(this).parent().css("padding-top","10px !important;");
		});   
	});
  		
  		 /**????????????????????????**/
  function convertTreeData(rows, textField) {
      for(var i = 0; i < rows.length; i++) {
          var row = rows[i];
          row.text = row[textField];
          if(row.children) {
          	row.state = "open";
              convertTreeData(row.children, textField);
          }
      }
  }
  /**???????????????????????????**/
  function joinTreeChildren(arr1, arr2) {
      for(var i = 0; i < arr1.length; i++) {
          var row1 = arr1[i];
          for(var j = 0; j < arr2.length; j++) {
              if(row1.id == arr2[j].id) {
                  var children = arr2[j].children;
                  if(children) {
                      row1.children = children;
                  }
                  
              }
          }
      }
  }
  </script>
  </#if>
</head>

 <body>
  <t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="${subsG['${key}'].entityName?uncap_first}Controller.do?doUpdate" tiptype="1">
	<input type="hidden" id="btn_sub" class="btn_sub"/>
	<input type="hidden" name="id" value='${'$'}{${subsG['${key}'].entityName?uncap_first}Page.id}' >
	<div class="tab-wrapper">
		<!-- tab -->
		<ul class="nav nav-tabs">
	      <li role="presentation" class="active"><a href="javascript:void(0);">${subsG['${key}'].ftlDescription}</a></li>
	    </ul>
		<!-- tab?????? -->	
		<div class="con-wrapper" id="con-wrapper1" style="display: block;">	
			<div class="row form-wrapper">
			<#list subPageNoAreatextColumnsMap['${key}'] as po>
				<#if (subPageNoAreatextColumnsMap['${key}']?size>10)>  
			    	<#if po_index%2==0>
				<div class="row show-grid">
					</#if>
			    <#else>
				<div class="row show-grid">
				</#if>
			    	<div class="col-xs-3 text-center">
		          		<b>${po.content}???</b>
		            </div>
			        <#if po.showType=='file' || po.showType == 'image'>
		          	<div class="col-xs-6">
		          	<#else>
		          	<div class="col-xs-3">
		          	</#if>
		          	
			        <#if subsG['${key}'].cgFormHead.isTree=='Y' && subsG['${key}'].cgFormHead.treeParentIdFieldNamePage==po.fieldName>
		        		<input id="${po.fieldName}" name="${po.fieldName}" type="text"  class="inputxt easyui-combotree" <@datatype validType="${po.fieldValidType!''}" isNull="${po.isNull}" type="${po.type}" mustInput="${po.fieldMustInput!''}"/>
								data-options="panelHeight:'220',
			                    url: '${subsG['${key}'].entityName?uncap_first}Controller.do?datagrid&field=id,${subsG['${key}'].cgFormHead.treeFieldnamePage}',  
			                    loadFilter: function(data) {
			                    	var rows = data.rows || data;
			                    	var win = frameElement.api.opener;
			                    	var listRows = win.getDataGrid().treegrid('getData');
			                    	joinTreeChildren(rows, listRows);
			                    	convertTreeData(rows, '${subsG['${key}'].cgFormHead.treeFieldnamePage}');
			                    	return rows; 
			                    },
			                     onSelect:function(node){
			                    	$('#${po.fieldName}').val(node.id);
			                    },
			                     onLoadSuccess: function() {
			                    	var win = frameElement.api.opener;
			                    	var currRow = win.getDataGrid().treegrid('getSelected');
			                    	if(!'${'$'}{${subsG['${key}'].entityName?uncap_first}Page.id}') {
			                    		//?????????????????????????????????
			                    		if(currRow) {
			                    			$('#${po.fieldName}').combotree('setValue', currRow.id);
			                    		}
			                    	}else {
			                    		//?????????????????????????????????
			                    		if(currRow) {
			                    			$('#${po.fieldName}').combotree('setValue', currRow.${po.fieldName});
			                    		}
			                    	}
			                    }"/>
			             
			         <#--  elseif po.showType=='tree'>
					 	<@treetag po = po formStyle="ace" opt = "update"/ -->
					 <#else>
			         	<@formControl po = po namepre="" valuepre = "${subsG['${key}'].entityName?uncap_first}Page."/>
			         </#if>
						<span class="Validform_checktip" style="float:left;height:0px;"></span>
						<label class="Validform_label" style="display: none">${po.content?if_exists?html}</label>
			        </div>
			        <#if (subPageNoAreatextColumnsMap['${key}']?size>10)>
						<#if (po_index%2==0)&&(!po_has_next)>
							<div class="col-xs-2 text-center"><b></b></div>
			         		<div class="col-xs-4"></div>
						</#if>
						<#if (po_index%2!=0)||(!po_has_next)>
					</div>
						</#if>
					<#else>
				</div>
					</#if>
			          
			        
			        </#list>
			        
			        <#assign ue_widget_count = 0>
					<#list subPageAreatextColumnsMap['${key}'] as po>
					<div class="row show-grid">
			          <div class="col-xs-3 text-center">
			          	<b>${po.content}???</b>
			          </div>
			          <div class="col-xs-6">
						    <#if po.showType=='textarea'>
						  	 	<textarea id="${po.fieldName}" class="form-control" rows="6" style="width:95%" name="${po.fieldName}" <@datatype validType="${po.fieldValidType!''}" isNull="${po.isNull}" type="${po.type}" mustInput="${po.fieldMustInput!''}"/>>${'$'}{${subsG['${key}'].entityName?uncap_first}Page.${po.fieldName}}</textarea>
							<#elseif po.showType='umeditor'>
								<#assign ue_widget_count = ue_widget_count + 1>
								<#if ue_widget_count == 1>
								<script type="text/javascript"  charset="utf-8" src="plug-in/ueditor/ueditor.config.js"></script>
								<script type="text/javascript"  charset="utf-8" src="plug-in/ueditor/ueditor.all.min.js"></script>
								</#if>
                                <textarea name="${po.fieldName}" id="${po.fieldName}" style="width: 650px;height:300px">${'$'}{${subsG['${key}'].entityName?uncap_first}Page.${po.fieldName} }</textarea>
							    <script type="text/javascript">
							        var ${po.fieldName}_editor = UE.getEditor('${po.fieldName}');
							    </script>
							</#if>
						<span class="Validform_checktip" style="float:left;height:0px;"></span>
						<label class="Validform_label" style="display: none">${po.content?if_exists?html}</label>
			          </div>
						</div>
			        </#list>
			         
			          
			            <div class="row" id = "sub_tr" style="display: none;">
				        <div class="col-xs-12 layout-header">
				          <div class="col-xs-6"></div>
				          <div class="col-xs-6"><button type="button" onclick="neibuClick();" class="btn btn-default">??????</button></div>
				        </div>
				      </div>
			     </div>
			   </div>
			   
			   <div class="con-wrapper" id="con-wrapper2" style="display: block;"></div>
			 </div>
  </t:formvalid>

<script type="text/javascript">
   $(function(){
    //?????????????????????,???????????????????????????????????????
	if(location.href.indexOf("load=detail")!=-1){
		$(".jeecgDetail").hide();
	}
	
	if(location.href.indexOf("mode=read")!=-1){
		//????????????????????????
		$("#formobj").find(":input").attr("disabled","disabled");
	}
	if(location.href.indexOf("mode=onbutton")!=-1){
		//??????????????????????????????
		$("#sub_tr").show();
	}
   });

  var neibuClickFlag = false;
  function neibuClick() {
	  neibuClickFlag = true; 
	  $('#btn_sub').trigger('click');
  }

</script>
 </body>
</html>