package com.googlecode.semrs.client.screens.dictionary;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.googlecode.semrs.client.ExtendedMessageBox;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.googlecode.semrs.client.screens.patient.ListPatientsScreen;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.CheckboxListenerAdapter;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtextux.client.widgets.form.ItemSelector;

public class AdminDiseaseScreen extends ShowcasePanel {

	 private com.gwtext.client.widgets.TabPanel tabPanel;
	  
	 private FormPanel diseaseForm = null;

	 private com.gwtext.client.widgets.Window editDiseaseWindow = null;

	 public static boolean reloadFlag = false;

	 private static String diseaseId;

	 //private static String deleteURL;

	 FieldDef[] fieldDefs = new FieldDef[] { 
			 new StringFieldDef("id"),
			 new StringFieldDef("name"), 
			 new StringFieldDef("description"), 
			 new StringFieldDef("lastEditDate"),	
			 new StringFieldDef("lastEditUser")
	 };

	 RecordDef recordDef = new RecordDef(fieldDefs);

	 JsonReader reader = new JsonReader("response.value.items", recordDef);

	 HttpProxy proxy = new HttpProxy("/semrs/diseaseServlet", Connection.GET);
	 final Store store = new Store(proxy,reader,true);
	 final PagingToolbar pagingToolbar = new PagingToolbar(store);

	 public AdminDiseaseScreen(){
		 //reader.setVersionProperty("response.value.version");
		 reader.setTotalProperty("response.value.total_count");
		 reader.setId("id");
	 }

	 protected void onActivate() {
		 if(reloadFlag){
			 store.load(0, pagingToolbar.getPageSize()); 
			 reloadFlag = false;
		 }
	 }

	 public Panel getViewPanel() {
		 if (panel == null) {
			 panel = new Panel();
			 MainPanel.resetTimer();


			 store.setDefaultSort("id", SortDir.ASC);
			 store.addStoreListener(new StoreListenerAdapter() {
				 public void onLoadException(Throwable error) {
					 //Check for session expiration
					 RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
					 try {
						 rb.sendRequest(null, new RequestCallback() {

							 public void onError(Request request, Throwable exception) {
								 MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de enfermedades.");
							 }
							 public void onResponseReceived(Request arg0, Response arg1) {
								 String errorMessage = arg1.getText();
								 if(errorMessage.indexOf("login") != -1){
									 MessageBox.alert("Error", "Su sesi&oacute;n de usuario ha expirado, presione OK para volver a loguearse." ,  
											 new MessageBox.AlertCallback() { 
										 public void execute() {
											 redirect("/semrs/");
										 }  
									 });  

								 }else{
									 MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de enfermedades.");
								 }
							 }
						 });
					 }catch (RequestException e) {
         	        	MessageBox.alert("Error", "Ha ocurrido un error al tratar de conectarse con el servidor.");
         	        }
				 }
				 public void onDataChanged(Store store) {
					 MainPanel.resetTimer();
				 }

			 });




			 final FormPanel formPanel = new FormPanel();
			 formPanel.setFrame(true);  
			 formPanel.setTitle("B&uacute;squeda de Enfermedades");  
			 formPanel.setWidth(900);  
			 formPanel.setLabelWidth(100); 
			 formPanel.setPaddings(5, 5, 5, 0);  
			 formPanel.setLabelAlign(Position.TOP);  
			 formPanel.setIconCls("disease-icon");

			 Panel topPanel = new Panel();  
			 topPanel.setLayout(new ColumnLayout());  

			 //create first panel and add fields to it  
			 Panel columnOnePanel = new Panel();  
			 columnOnePanel.setLayout(new FormLayout());  

			 TextField id = new TextField("C&oacute;digo", "id");
			 columnOnePanel.add(id, new AnchorLayoutData("65%"));  

			 //add first panel as first column with 50% of the width  
			 topPanel.add(columnOnePanel, new ColumnLayoutData(.5));  

			 //create second panel and add fields to it  

			 Panel columnTwoPanel = new Panel();  
			 columnTwoPanel.setLayout(new FormLayout());  

			 TextField diseaseName = new TextField("Nombre", "name");  
			 columnTwoPanel.add(diseaseName, new AnchorLayoutData("65%")); 
			 topPanel.add(columnTwoPanel, new ColumnLayoutData(0.5));  
			 /*
    TextField drugDesc = new TextField("Descripci&oacute;n", "description");   
    columnTwoPanel.add(drugDesc, new AnchorLayoutData("65%"));  


    TextField lastName = new TextField("Apellido", "lastName");  
    columnTwoPanel.add(lastName, new AnchorLayoutData("65%"));  
			  */
			 //add the second panel as the second column to the top panel to take up the other 50% width  


			 FieldSet fieldSet = new FieldSet();
			 fieldSet.add(topPanel);


			 Panel proxyPanel = new Panel();  
			 proxyPanel.setBorder(true);  
			 proxyPanel.setBodyBorder(false);
			 proxyPanel.setCollapsible(false);  
			 proxyPanel.setLayout(new FormLayout());
			 proxyPanel.setButtonAlign(Position.CENTER);

			 Button clear = new Button("Limpiar");
			 clear.setIconCls("clear-icon");
			 clear.addListener(new ButtonListenerAdapter(){

				 public void onClick(Button button, EventObject e){
					 formPanel.getForm().reset();

				 }

			 });
			 proxyPanel.addButton(clear);  

			 final Button search = new Button("Buscar");  
			 search.setIconCls("search-icon");  
			 search.addListener(new ButtonListenerAdapter(){

				 public void onClick(Button button, EventObject e){
					 UrlParam[] params = getFormData(formPanel.getForm());
					 store.setBaseParams(params);
					 store.load(0, pagingToolbar.getPageSize());
					 //store.removeAll();
					 //store.reload(params);
					 //store.commitChanges();
					 pagingToolbar.updateInfo();
					 MainPanel.resetTimer();
				 }

			 });
			 proxyPanel.addButton(search);  
			 fieldSet.add(proxyPanel);
			 formPanel.add(fieldSet);  
			 // formPanel.add(proxyPanel);

			 formPanel.setMonitorValid(true);
			 formPanel.addListener(new FormPanelListenerAdapter() {
				 public void onClientValidation(FormPanel formPanel, boolean valid) {
					 search.setDisabled(!valid);
				 }
			 });






			 GridView view = new GridView();
			 view.setEmptyText("No hay Registros");
			 view.setAutoFill(true);
			 view.setForceFit(true);

			 GridPanel grid = new GridPanel(store, createColModel());
			 grid.setEnableDragDrop(false);
			 grid.setWidth(850);
			 grid.setHeight(420);
			 grid.setTitle("Lista de Enfermedades");
			 grid.setLoadMask(true);  
			 grid.setSelectionModel(new RowSelectionModel());  
			 grid.setFrame(true);  
			 grid.setView(view);
			 grid.addGridCellListener(new GridCellListener() {  


				 public void onCellDblClick(GridPanel grid, int rowIndex,
						 int colIndex, EventObject e) {
					 Record r = grid.getStore().getAt(rowIndex);
					 String recordId = r.getAsString("id");
					 setDiseaseId(recordId);
					 //diseaseId = recordId;
					 com.gwtext.client.widgets.Window diseaseWindow = getEditDiseaseWindow(false);
					 diseaseWindow.show();
					 //getEditGroupWindow(diseaseId).show();
				 }


				 public void onCellClick(GridPanel grid, int rowIndex,
						 int colIndex, EventObject e) {
					 final Record r = grid.getStore().getAt(rowIndex);
					 String recordId = r.getAsString("id");

				 }


				 public void onCellContextMenu(GridPanel grid, int rowIndex,
						 int cellIndex, EventObject e) {
					 // TODO Auto-generated method stub

				 }
			 });  


			 pagingToolbar.setPageSize(20);
			 pagingToolbar.setDisplayInfo(true);
			 pagingToolbar.setEmptyMsg("No hay registros");


			 NumberField pageSizeField = new NumberField();
			 pageSizeField.setWidth(40);
			 pageSizeField.setSelectOnFocus(true);
			 pageSizeField.addListener(new FieldListenerAdapter() {
				 public void onSpecialKey(Field field, EventObject e) {
					 if (e.getKey() == EventObject.ENTER) {
						 int pageSize = Integer.parseInt(field.getValueAsString());
						 pagingToolbar.setPageSize(pageSize);
					 }
				 }
			 });

			 ToolTip toolTip = new ToolTip("Introduzca el tama&ntilde;o de p&aacute;gina");
			 toolTip.applyTo(pageSizeField);

			 pagingToolbar.addField(pageSizeField);
			 pagingToolbar.addSeparator();
			 ToolbarButton newLabTestWindow = new ToolbarButton("Nueva Enfermedad", new ButtonListenerAdapter() {  
				 public void onClick(Button button, EventObject e) {
					 setDiseaseId("");
					 //diseaseId = "";
					 com.gwtext.client.widgets.Window diseaseWindow = getEditDiseaseWindow(true);
					 diseaseWindow.show();
					 // getEditGroupWindow("").show();
				 }  
			 });  
			 newLabTestWindow.setIconCls("add-icon");
			 pagingToolbar.addButton(newLabTestWindow);
			 pagingToolbar.addSeparator();

			 //pagingToolbar.addButton(deleteGroupButton);
			 //pagingToolbar.addSeparator();
			 ToolbarButton exportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
				 public void onClick(Button button, EventObject e) {
					 Window.open("/semrs/diseaseServlet?export=true", "_self", ""); 

				 }  
			 });  
			 exportButton.setIconCls("excel-icon");
			 pagingToolbar.addButton(exportButton);
			 pagingToolbar.addSeparator();
			 pagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
			 grid.setBottomToolbar(pagingToolbar);


			 grid.addListener(new PanelListenerAdapter() {  
				 public void onRender(Component component) {  
					 store.load(0, pagingToolbar.getPageSize()); 
				 }  
			 }); 
			 formPanel.add(grid, new AnchorLayoutData("100%"));

			 panel.add(formPanel);

		 }


		 return panel;
	 }

	 static ColumnModel createColModel() {

		 ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
				 new ColumnConfig("C&oacute;digo", "id"),
				 new ColumnConfig("Nombre", "name"), 
				 // new ColumnConfig("Descripci&oacute;n", "description"), 
				 new ColumnConfig("F.Modificaci&oacute;n", "lastEditDate"),
				 new ColumnConfig("U.Modificaci&oacute;n", "lastEditUser")

		 });
		 for (int i = 0; i < colModel.getColumnConfigs().length; i++){
			 ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
		 }
		 return colModel;
	 }

	 public com.gwtext.client.widgets.TabPanel getTabPanel() {
		 return tabPanel;
	 }

	 public void setTabPanel(com.gwtext.client.widgets.TabPanel tabPanel) {
		 this.tabPanel = tabPanel;
	 }


	 public com.gwtext.client.widgets.Window getEditDiseaseWindow(boolean isNew){

		 if(editDiseaseWindow!=null){
			 editDiseaseWindow.clear();
		 }

		 editDiseaseWindow = new com.gwtext.client.widgets.Window();  
		 editDiseaseWindow.setTitle("Editar Enfermedad");  
		 editDiseaseWindow.setWidth(700);  
		 editDiseaseWindow.setHeight(600);    
		 editDiseaseWindow.setLayout(new FitLayout());  
		 editDiseaseWindow.setPaddings(5);  
		 editDiseaseWindow.setResizable(true);
		 editDiseaseWindow.setButtonAlign(Position.CENTER);  
		 editDiseaseWindow.setModal(true);
		 editDiseaseWindow.setId("editDiseaseWindow");
		 editDiseaseWindow.setIconCls("disease-icon");
		 editDiseaseWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);  
		 editDiseaseWindow.setMaximizable(true);
		 //editDiseaseWindow.setMinimizable(true);

		 RecordDef recordDef = new RecordDef(new FieldDef[]{   
				 new StringFieldDef("id"),  
				 new StringFieldDef("name"),  
				 new StringFieldDef("description"),
				 new StringFieldDef("sex"),
				 new StringFieldDef("ages"),
				 new StringFieldDef("toxicHabits"),
				 new StringFieldDef("loadSuccess")
		 });  

		 final JsonReader reader = new JsonReader("data", recordDef);  
		 reader.setSuccessProperty("success"); 
		 reader.setId("id");

		 //setup error reader to process from submit response from server  
		 RecordDef errorRecordDef = new RecordDef(new FieldDef[]{  
				 new StringFieldDef("id"),  
				 new StringFieldDef("msg")  
		 });  

		 final JsonReader errorReader = new JsonReader("field", errorRecordDef);  
		 errorReader.setSuccessProperty("success"); 

		 if(diseaseForm!=null){
			 diseaseForm.clear();
		 }

		 diseaseForm = new FormPanel(); 

		 diseaseForm.setReader(reader);  
		 diseaseForm.setErrorReader(errorReader); 
		 diseaseForm.setFrame(true);  
		 diseaseForm.setWidth(700);  
		 diseaseForm.setHeight(600);
		 diseaseForm.setAutoScroll(true);
		 diseaseForm.setId("editDiseaseForm");

		 Panel proxyPanel = new Panel();  
		 proxyPanel.setBorder(true);  
		 proxyPanel.setBodyBorder(false);
		 proxyPanel.setCollapsible(false);  
		 proxyPanel.setLayout(new FormLayout());
		 proxyPanel.setButtonAlign(Position.CENTER);
		 //proxyPanel.setIconCls("groupProxyPanel");

		 FieldSet diseaseFS = new FieldSet("Informaci&oacute;n de Enfermedad");  
		 diseaseFS.setCollapsible(true);
		 diseaseFS.setFrame(false);  
		 diseaseFS.setId("diseaseFS");

		 TextField diseaseIdText = new TextField("C&oacute;digo","id",190);
		 diseaseIdText.setId("diseaseIdText");
		 if(!getDiseaseId().equals("")){
			 diseaseIdText.setReadOnly(true);
		 }
		 diseaseIdText.setAllowBlank(false);
		 diseaseIdText.setStyle("textTransform: uppercase;");
		 diseaseIdText.addListener(new FieldListenerAdapter(){
			 public void onBlur(Field field) {
				 String value = field.getValueAsString();
				 field.setValue(value.toUpperCase());
			 }

		 });
		 diseaseFS.add(diseaseIdText);  
		 
		 TextField loadSuccess = new TextField("loadSuccess","loadSuccess",190);
	     loadSuccess.setId("diseaseLoadSuccess");
	     loadSuccess.setVisible(false);
	     diseaseFS.add(loadSuccess); 

		 TextField diseaseNameText = new TextField("Nombre", "name", 190);  
		 diseaseNameText.setId("diseaseNameText");
		 diseaseNameText.setAllowBlank(false);
		 diseaseFS.add(diseaseNameText); 
		 
		//add a ComboBox field  
		 Store sexStore = new SimpleStore(new String[]{"abbr", "sex"}, new String[][]{
				 new String[]{"Indiferente","Indiferente"},new String[]{"M","Masculino"}, new String[]{"F", "Femenino"}});  
		 sexStore.load();  

		 final ComboBox sexCB = new ComboBox();  
		 sexCB.setFieldLabel("Sexo");  
		 sexCB.setHiddenName("sex");  
		 sexCB.setStore(sexStore);  
		 sexCB.setDisplayField("sex");  
		 sexCB.setTypeAhead(true);  
		 sexCB.setMode(ComboBox.LOCAL);  
		 sexCB.setTriggerAction(ComboBox.ALL);  
		 sexCB.setSelectOnFocus(true);  
		 sexCB.setWidth(190); 
		 sexCB.setEmptyText("Indiferente");
		 sexCB.setName("sex"); 
		 sexCB.setId("diseaseSexCB");
		 diseaseFS.add(sexCB); 
		 
			//add a ComboBox field  
		 Store agesStore = new SimpleStore(new String[]{"agesid", "ages"}, 
				 new String[][]{new String[]{"Indiferente","Indiferente"},new String[]{"0-10","0-10"}, new String[]{"10-20", "10-20"},
				 new String[]{"20-30", "20-30"},new String[]{"30-40", "30-40"},
				 new String[]{"40-50", "40-50"},new String[]{"50-60", "50-60"},
				 new String[]{"60 +", "60 +"}});  
		 agesStore.load();  

		 final ComboBox agesCB = new ComboBox();  
		 agesCB.setFieldLabel("Rango de Edad");  
		 agesCB.setHiddenName("ages");  
		 agesCB.setStore(agesStore);  
		 agesCB.setDisplayField("ages");  
		 agesCB.setTypeAhead(true);  
		 agesCB.setMode(ComboBox.LOCAL);  
		 agesCB.setTriggerAction(ComboBox.ALL);  
		 agesCB.setSelectOnFocus(true);  
		 agesCB.setWidth(190); 
		 agesCB.setEmptyText("Indiferente");
		 agesCB.setName("ages"); 
		 agesCB.setId("diseaseAgesCB");
		 agesCB.setReadOnly(true);
		 diseaseFS.add(agesCB); 
		 
		 
		 Store toxicHabitsStore = new SimpleStore(new String[]{"toxicHabitValue", "toxicHabits"}, new String[][]{ new String[]{"Si","Si"}, new String[]{"No", "No"}});  
		 toxicHabitsStore.load();  

		 final ComboBox toxicHabitsCB = new ComboBox();  
		 toxicHabitsCB.setFieldLabel("H&aacute;bitos T&oacute;xicos");  
		 toxicHabitsCB.setHiddenName("toxicHabits");  
		 toxicHabitsCB.setStore(toxicHabitsStore);  
		 toxicHabitsCB.setDisplayField("toxicHabits");  
		 toxicHabitsCB.setTypeAhead(true);  
		 toxicHabitsCB.setMode(ComboBox.LOCAL);  
		 toxicHabitsCB.setTriggerAction(ComboBox.ALL);  
		 toxicHabitsCB.setSelectOnFocus(true);  
		 toxicHabitsCB.setWidth(190); 
		 toxicHabitsCB.setEmptyText("No");
		 toxicHabitsCB.setName("toxicHabits"); 
		 toxicHabitsCB.setId("diseaseToxicHabitsCB");
		 toxicHabitsCB.setReadOnly(true);
		 diseaseFS.add(toxicHabitsCB);


		 HtmlEditor diseaseDesc = new HtmlEditor("Descripci&oacute;n", "description");  
		 diseaseDesc.setId("diseaseDesc");
		 // drugDesc.setWidth(190);
		 diseaseDesc.setHeight(200);  
		 diseaseFS.add(diseaseDesc); 

		 diseaseForm.add(diseaseFS);

		 FieldSet diseaseSymptomsFS = new FieldSet("S&iacute;ntomas");  
		 diseaseSymptomsFS.setId("diseaseSymptomsFS");
		 diseaseSymptomsFS.setCollapsible(true);
		 diseaseSymptomsFS.setFrame(false);  
		 
		 FieldSet diseaseLabTestsFS = new FieldSet("Ex&aacute;menes de Laboratorio");  
		 diseaseLabTestsFS.setId("diseaseLabTestsFS");
		 diseaseLabTestsFS.setCollapsible(true);
		 diseaseLabTestsFS.setFrame(false);  
		 
		 FieldSet diseaseProceduresFS = new FieldSet("Procedimientos");  
		 diseaseProceduresFS.setId("diseaseProceduresFS");
		 diseaseProceduresFS.setCollapsible(true);
		 diseaseProceduresFS.setFrame(false);  
		 
		 FieldSet relatedDiseasesFS = new FieldSet("Enfermedades");  
		 relatedDiseasesFS.setId("relatedDiseasesFSD");
		 relatedDiseasesFS.setCollapsible(true);
		 relatedDiseasesFS.setFrame(false);  

		   /*Symptoms*/
		   FieldDef[] gridFieldDefs = new FieldDef[] { 
		     		new StringFieldDef("id"),
		            new StringFieldDef("name")
		     };

		    RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		    JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		    HttpProxy gridProxy = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getSymptoms&diseaseId="+getDiseaseId()+"&diseaseSymptoms=true", Connection.GET);
		    final Store gridStore = new Store(gridProxy,gridReader,true);
		   
		 
		    
			FieldDef[] gridFieldDefs2 = new FieldDef[] { 
	  		     		new StringFieldDef("id"),
	  		            new StringFieldDef("name")
	  		  };

	  		RecordDef gridRecordDef2 = new RecordDef(gridFieldDefs2);
	  		JsonReader gridReader2 = new JsonReader("response.value.items", gridRecordDef2);
	  		HttpProxy gridProxy2 = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getSymptoms&diseaseId="+getDiseaseId(), Connection.GET);
	  		final Store innerGridStore = new Store(gridProxy2,gridReader2,true);
			
	  		
	  		/*LabTests*/
	  		FieldDef[] gridFieldDefsL = new FieldDef[] { 
		     		new StringFieldDef("id"),
		            new StringFieldDef("name")
		     };

		    RecordDef gridRecordDefL = new RecordDef(gridFieldDefsL);
		    JsonReader gridReaderL = new JsonReader("response.value.items", gridRecordDefL);
		    HttpProxy gridProxyL = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getLabTests&diseaseId="+getDiseaseId()+"&diseaseLabTests=true", Connection.GET);
		    final Store gridStoreL = new Store(gridProxyL,gridReaderL,true);
		   
		 
		    
			FieldDef[] gridFieldDefsL2 = new FieldDef[] { 
	  		     		new StringFieldDef("id"),
	  		            new StringFieldDef("name")
	  		  };

	  		RecordDef gridRecordDefL2 = new RecordDef(gridFieldDefsL2);
	  		JsonReader gridReaderL2 = new JsonReader("response.value.items", gridRecordDefL2);
	  		HttpProxy gridProxyL2 = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getLabTests&diseaseId="+getDiseaseId(), Connection.GET);
	  		final Store innerGridStoreL = new Store(gridProxyL2,gridReaderL2,true);
			  
	  		
	  		/*Procedures*/
	        FieldDef[] gridFieldDefsP = new FieldDef[] { 
		     		new StringFieldDef("id"),
		            new StringFieldDef("name")
		     };

		    RecordDef gridRecordDefP = new RecordDef(gridFieldDefsP);
		    JsonReader gridReaderP = new JsonReader("response.value.items", gridRecordDefP);
		    HttpProxy gridProxyP = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getProcedures&diseaseId="+getDiseaseId()+"&diseaseProcedures=true", Connection.GET);
		    final Store gridStoreP = new Store(gridProxyP,gridReaderP,true);
		   
		    
			   FieldDef[] gridFieldDefsP2 = new FieldDef[] { 
	  		     		new StringFieldDef("id"),
	  		            new StringFieldDef("name")
	  		     };

	  	    RecordDef gridRecordDefP2 = new RecordDef(gridFieldDefsP2);
	  		JsonReader gridReaderP2 = new JsonReader("response.value.items", gridRecordDefP2);
	  		HttpProxy gridProxyP2 = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getProcedures&diseaseId="+getDiseaseId(), Connection.GET);
	  		final Store innerGridStoreP = new Store(gridProxyP2,gridReaderP2,true);
		 
	  		
	  		/*Diseases*/
	  		FieldDef[] gridFieldDefsD = new FieldDef[] { 
		     		new StringFieldDef("id"),
		            new StringFieldDef("name")
		     };

		    RecordDef gridRecordDefD = new RecordDef(gridFieldDefsD);
		    JsonReader gridReaderD = new JsonReader("response.value.items", gridRecordDefD);
		    HttpProxy gridProxyD = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getRelatedDiseases&diseaseId="+getDiseaseId()+"&relatedDiseases=true", Connection.GET);
		    final Store gridStoreD = new Store(gridProxyD,gridReaderD,true);
		   
		    
			   FieldDef[] gridFieldDefsD2 = new FieldDef[] { 
	  		     		new StringFieldDef("id"),
	  		            new StringFieldDef("name")
	  		     };

	  	    RecordDef gridRecordDefD2 = new RecordDef(gridFieldDefsD2);
	  		JsonReader gridReaderD2 = new JsonReader("response.value.items", gridRecordDefD2);
	  		HttpProxy gridProxyD2 = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getRelatedDiseases&diseaseId="+getDiseaseId(), Connection.GET);
	  		final Store innerGridStoreD = new Store(gridProxyD2,gridReaderD2,true);
	  		
	  		
	  		
	     gridStore.load();
	     gridStoreL.load();
	     gridStoreP.load();
	     gridStoreD.load();
		 
		 
	     diseaseSymptomsFS.add(getGrid(gridStore,innerGridStore ,createColModel(false,false), "S&iacute;ntomas Relacionados", "symptoms-icon"), new AnchorLayoutData("100%"));

	     diseaseLabTestsFS.add(getGrid(gridStoreL,innerGridStoreL ,createColModel(false,false), "Ex&aacute;menes Relacionados", "labtests-icon"), new AnchorLayoutData("100%"));

	     diseaseProceduresFS.add(getGrid(gridStoreP,innerGridStoreP ,createColModel(false,false), "Procedimientos Relacionados", "procedure-icon"), new AnchorLayoutData("100%"));

	     relatedDiseasesFS.add(getGrid(gridStoreD,innerGridStoreD ,createColModel(false,false), "Enfermedades Relacionadas", "disease-icon"), new AnchorLayoutData("100%"));

		 
		 
		 
		 diseaseForm.add(diseaseSymptomsFS);
		 
		 diseaseForm.add(diseaseLabTestsFS);
		 
		 diseaseForm.add(diseaseProceduresFS);
		 
		 diseaseForm.add(relatedDiseasesFS);
		 
		
		 
		 



		 final Button saveButton = new Button("Guardar");
		 saveButton.setId("diseaseSaveButton");
		 saveButton.addListener(new ButtonListenerAdapter(){
			 public void onClick(final Button button, EventObject e){

				 MessageBox.show(new MessageBoxConfig() {  
					 {  
						 setMsg("Guardando los cambios, por favor espere...");  
						 setProgressText("Guardando...");  
						 setWidth(300);  
						 setWait(true);  
						 setWaitConfig(new WaitConfig() {  
							 {  
								 setInterval(200);  
							 }  
						 });  
						 setAnimEl(button.getId());  
					 }  
				 });  


				 try {
					 RequestBuilder saveRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/diseaseServlet?diseaseEdit=submit&isNew="+getDiseaseId()
							 +"&"+diseaseForm.getForm().getValues()
							 +"&diseaseSymptoms="+getRecordValues(gridStore.getRecords())
							 +"&diseaseLabTests="+getRecordValues(gridStoreL.getRecords())
							 +"&diseaseProcedures="+getRecordValues(gridStoreP.getRecords())
							 +"&relatedDiseasesD="+getRecordValues(gridStoreD.getRecords()));
					 saveRequest.sendRequest(null, new RequestCallback() {
						 public void onResponseReceived(Request req, Response res) {
							 MessageBox.hide();  
							 MessageBox.getDialog().close();
							 if(res.getText().indexOf("errores") !=-1){
								 MessageBox.hide();  
								 MessageBox.alert("Error", res.getText()); 
							 }else if(res.getText().equals("")){
								 MessageBox.hide();
								 MessageBox.alert("Error", "Error interno"); 
							 }else{
								 MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());
								 MainPanel.resetTimer();
								 editDiseaseWindow.hide();
								 store.reload();
							 }
						 }

						 public void onError(Request req, Throwable exception) {
							 MessageBox.hide();  
							 MessageBox.getDialog().close();
							 MessageBox.alert("Error", "Error interno"); 
						 }

					 });

				 } catch (RequestException re) {
					 MessageBox.hide();  
					 MessageBox.getDialog().close();
					 MessageBox.alert("Error", "Error interno"); 
				 }
			 }

		 });
		 saveButton.setIconCls("save-icon");
		 proxyPanel.addButton(saveButton);


		 Button delete = new Button("Eliminar");
		 delete.setId("diseaseDeleteButton");
		 if(getDiseaseId().equals("")){
			 delete.setDisabled(true);
		 }else{
			 delete.setDisabled(false);
		 }
		 final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/diseaseServlet?diseaseEdit=delete&id="+getDiseaseId());
		 delete.addListener(new ButtonListenerAdapter(){
			 public void onClick(final Button button, EventObject e){

				 ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar esta Enfermedad?", "Si", "No",  
						 new MessageBox.ConfirmCallback() {  
					 public void execute(String btnID) {
						 if(btnID.equals("yes")){
							 MessageBox.show(new MessageBoxConfig() {  
								 {  
									 setMsg("Eliminando registro, por favor espere...");  
									 setProgressText("Eliminando...");  
									 setWidth(300);  
									 setWait(true);  
									 setWaitConfig(new WaitConfig() {  
										 {  
											 setInterval(200);  
										 }  
									 });  
									 setAnimEl(button.getId());  
								 }  
							 });  


							 try {

								 rb.sendRequest(null, new RequestCallback() {
									 public void onResponseReceived(Request req, Response res) {
										 MessageBox.hide();  
										 MessageBox.getDialog().close(); 
										 if(res.getText().indexOf("errores") !=-1){
											 MessageBox.alert("Error", res.getText()); 
										 }else if(res.getText().equals("")){
											 MessageBox.hide();
											 MessageBox.alert("Error", "Error interno"); 
										 }else{
											 MainPanel.resetTimer();
											 MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText() ,  
													 new MessageBox.AlertCallback() { 
												 public void execute() {
													 editDiseaseWindow.hide();
													 store.reload();
												 }  
											 });  

										 }


									 }

									 public void onError(Request req, Throwable exception) {
										 MessageBox.hide();  
										 MessageBox.getDialog().close();
										 MessageBox.alert("Error", "Error interno"); 
									 }

								 });

							 } catch (RequestException re) {
								 MessageBox.hide();  
								 MessageBox.getDialog().close();
								 MessageBox.alert("Error", "Error interno"); 
							 }
						 }
					 }  
				 });  

			 }

		 });
		 delete.setIconCls("delete-icon");
		 proxyPanel.addButton(delete);  


		 Button cancel = new Button("Cancelar");
		 cancel.setId("cancelDiseaseButton");
		 cancel.addListener(new ButtonListenerAdapter(){
			 public void onClick(Button button, EventObject e){
				 editDiseaseWindow.hide();
			 }

		 });
		 cancel.setIconCls("cancel-icon");
		 proxyPanel.addButton(cancel);  

		 diseaseForm.add(proxyPanel);

		 diseaseForm.setMonitorValid(true);
		 diseaseForm.addListener(new FormPanelListenerAdapter() {
			 public void onClientValidation(FormPanel formPanel, boolean valid) {
				 saveButton.setDisabled(!valid);
			 }
		 });
		 diseaseForm.doLayout();
		 
		 if(!isNew){
			 diseaseForm.getForm().load("/semrs/diseaseServlet?diseaseEdit=load&id="+getDiseaseId(), null, Connection.GET, "Cargando...");
			 diseaseForm.getForm().addListener(new FormListenerAdapter(){
		    	   public void onActionComplete(Form form, int httpStatus, String responseText) {
		    		  if(form.findField("diseaseLoadSuccess").getValueAsString().equals("false")){
		    				MessageBox.show(new MessageBoxConfig() {  
		    					{  
		    						setTitle("Error");
		    						setMsg("Esta enfermedad no existe");
		    						setIconCls(MessageBox.ERROR);
		    					    setModal(true);
		    					    setButtons(MessageBox.OK);
		    					    setCallback(new MessageBox.PromptCallback() { 
		    						public void execute(
		    								String btnID,
		    								String text) {
		    							store.reload();
		    							editDiseaseWindow.close();
		    							
		    						}  
		                            });  
		    					}  
		    				});
		    			  
		    		  }    
		    	   }
		    	   public void onActionFailed(Form form, int httpStatus, String responseText) {
		   			MessageBox.show(new MessageBoxConfig() {  
						{  
							setTitle("Error");
							setMsg("Ocurrio un error al tratar de obtener esta enfermedad");
							setIconCls(MessageBox.ERROR);
						    setModal(true);
						    setButtons(MessageBox.OK);
						    setCallback(new MessageBox.PromptCallback() { 
							public void execute(
									String btnID,
									String text) {
								store.reload();
								editDiseaseWindow.close();
								
							}  
	                        });  
						}  
					});
		    	    }
		    	   
		       });
		      }
		 editDiseaseWindow.add(diseaseForm);  
		 editDiseaseWindow.doLayout();
		 return editDiseaseWindow;
	 }

	 public String getDiseaseId() {
		 return diseaseId;
	 }

	 public void setDiseaseId(String diseaseId) {
		 this.diseaseId = diseaseId;
	 }


}
