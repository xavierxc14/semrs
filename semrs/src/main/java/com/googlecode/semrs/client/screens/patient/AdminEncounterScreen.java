package com.googlecode.semrs.client.screens.patient;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.googlecode.semrs.client.ExtendedMessageBox;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.googlecode.semrs.client.screens.admin.AdminUsersScreen;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtextux.client.widgets.tree.FileTreePanel;
import com.gwtext.client.widgets.tree.TreeNode;  
import com.gwtextux.client.widgets.tree.FileTreePanelOpenAction;

public class AdminEncounterScreen extends ShowcasePanel{

	private String encounterId = "";
	
	private boolean edit = true;
	
	public static boolean flag1 = false;
	
	private Store currentEncountersStore;
	
	private Store previousEncountersStore;
	
	FormPanel formPanel = null;
	
	protected void onActivate() {
		
		if(!flag1){
			getViewPanel();
		}
		flag1=false;
	}

	public Panel getViewPanel() {
		
		flag1=true;

		if(formPanel!=null){
		   formPanel.clear();
		}

		panel = new Panel();
		formPanel = new FormPanel();  
		formPanel.setId("encounterDetailForm");
		formPanel.setTitle("Detalle de Consulta");  
		formPanel.setFrame(true);  
		formPanel.setPaddings(5, 0, 5, 5);  
		formPanel.setWidth(1135);  
		formPanel.setLabelWidth(100);
		formPanel.setIconCls("encounter-icon");
		MainPanel.resetTimer();
		
		RecordDef formRecordDef = new RecordDef(new FieldDef[]{   
				new StringFieldDef("id"),  
				new StringFieldDef("patientId"),  
				new StringFieldDef("patientWeight"),
				new StringFieldDef("patientWeightUnits"),
				new StringFieldDef("patientHeight"),
				new StringFieldDef("patientHeightUnits"),  
				new StringFieldDef("patientName"),  
				new StringFieldDef("patientLastName"),
				new StringFieldDef("patientSex"),
				new StringFieldDef("patientAge"),
				new StringFieldDef("patientAgeH"),
				new StringFieldDef("encounterRefferral"),
				new StringFieldDef("encounterReason"),
				new StringFieldDef("background"),
				new StringFieldDef("toxicHabits"),
				new StringFieldDef("toxicHabitsDesc"),
				new StringFieldDef("loadSuccess"),
				new StringFieldDef("loadPatientSuccess")
		});  

		final JsonReader formReader = new JsonReader("data", formRecordDef);  
		formReader.setSuccessProperty("success"); 
		formReader.setId("id");
		
		HttpProxy loadProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getEncounter&id="+getEncounterId()+"&edit="+isEdit(), Connection.GET);
		Store formStore = new Store(loadProxy,formReader);
		formStore.load();
		
		
		Panel proxyPanel = new Panel();  
		proxyPanel.setBorder(true);  
		proxyPanel.setBodyBorder(false);
		proxyPanel.setCollapsible(false);  
		proxyPanel.setLayout(new FormLayout());
		proxyPanel.setButtonAlign(Position.CENTER);
		
		FieldSet patientFS = new FieldSet("Informaci&oacute;n de Paciente");  
		patientFS.setFrame(false);  
		patientFS.setAutoWidth(true);
		patientFS.setAutoHeight(true);
		patientFS.setCollapsible(true);
		patientFS.setAnimCollapse(true);
		
		final TextField encounterIdText = new TextField("id","id");
		encounterIdText.setId("encounterIdTextEncounterEdit");
		encounterIdText.setVisible(false);
		patientFS.add(encounterIdText);
		
		
		final TextField patientIdText = new TextField("C&eacute;dula","patientId",90);
		patientIdText.setId("patientIdTextEncounterEdit");
		patientIdText.setReadOnly(true);
		patientFS.add(patientIdText);
		
		final TextField patientNameText= new TextField("Nombres","patientName",260);
		patientNameText.setId("patientNameTextEncounterEdit");
		patientNameText.setReadOnly(true);
		patientFS.add(patientNameText);
		
		final TextField patientLastNameText= new TextField("Apellidos","patientLastName",260);
		patientLastNameText.setId("patientLastNameTextEncounterEdit");
		patientLastNameText.setReadOnly(true);
		patientFS.add(patientLastNameText);
		
		final TextField patientAgeText= new TextField("Edad","patientAge");
		patientAgeText.setId("patientAgeTextEncounterEdit");
		patientAgeText.setReadOnly(true);
		patientFS.add(patientAgeText);
		
		final Hidden patientAgeHidden= new Hidden();
		patientAgeHidden.setId("patientAgeHEncounterEdit");
		patientAgeHidden.setName("patientAgeH");
		patientFS.add(patientAgeHidden);
		
		final TextField patientSexText= new TextField("Sexo","patientSex");
		patientSexText.setId("patientSexTextTextEncounterEdit");
		patientSexText.setReadOnly(true);
		patientFS.add(patientSexText);
		
		Store toxicHabitsStore = new SimpleStore(new String[]{"id", "desc"}, new String[][]{ new String[]{"Si","Si"}, new String[]{"No", "No"}});  
		toxicHabitsStore.load();  

		final ComboBox toxicHabitsCB = new ComboBox();  
		toxicHabitsCB.setFieldLabel("Habitos T&oacute;xicos");  
		toxicHabitsCB.setId("toxicHabitsCBEncounterEdit");
		toxicHabitsCB.setHiddenName("toxicHabits");  
		toxicHabitsCB.setStore(toxicHabitsStore);  
		toxicHabitsCB.setDisplayField("desc");  
		toxicHabitsCB.setTypeAhead(true);  
		toxicHabitsCB.setMode(ComboBox.LOCAL);  
		toxicHabitsCB.setTriggerAction(ComboBox.ALL);  
		toxicHabitsCB.setSelectOnFocus(false);  
		toxicHabitsCB.setWidth(85); 
		toxicHabitsCB.setEmptyText(" ");
		toxicHabitsCB.setName("toxicHabits");
		toxicHabitsCB.setReadOnly(true);
		if(!isEdit()){
			toxicHabitsCB.setDisabled(true);
		}
		
		
		final TextArea toxicHabitsDesc = new TextArea("Descripci&oacute;n", "toxicHabitsDesc"); 
		toxicHabitsDesc.setId("toxicHabitsDescEncounterEdit");
		toxicHabitsDesc.setHideLabel(false);  
		toxicHabitsDesc.setWidth(190);
		toxicHabitsDesc.setHeight(80);
		toxicHabitsDesc.setVisible(false);
		if(!isEdit()){
			toxicHabitsDesc.setReadOnly(true);
		}
		
		toxicHabitsCB.addListener(new ComboBoxListenerAdapter(){
			   public void onSelect(ComboBox comboBox, Record record, int index) {
				   
				   if(record.getAsString("id").equals("Si")){
					   toxicHabitsDesc.reset();
					   toxicHabitsDesc.setVisible(true);
				   }else{
					   toxicHabitsDesc.setVisible(false);
				   }
				   
			    }
		});
		
		
		
		
		final TextField encounterRefferral= new TextField("Referido de","encounterRefferral");
		encounterRefferral.setId("encounterRefferralEncounterEdit");
		encounterRefferral.setReadOnly(true);
		patientFS.add(encounterRefferral);
		
		final TextField encounterReason= new TextField("Motivo de Consulta","encounterReason",260);
		encounterReason.setId("encounterReasonEncounterEdit");
	    encounterReason.setReadOnly(true);

		patientFS.add(encounterReason);
		
		MultiFieldPanel weightPanel = new MultiFieldPanel();
		final NumberField patientWeight= new NumberField("Peso","patientWeight", 90);
		patientWeight.setId("patientWeightEncounterEdit");
		patientWeight.setAllowNegative(false);
		if(!isEdit()){
			patientWeight.setReadOnly(true);
		}
		weightPanel.addToRow(patientWeight, 210);  
		Store weightStore = new SimpleStore(new String[]{"id", "desc"}, new String[][]{ new String[]{"Gramos","Gramos"}, 
				new String[]{"Kilogramos", "Kilogramos"},new String[]{"Libras", "Libras"}});  
		weightStore.load();  
		final ComboBox weightCB = new ComboBox();  
		weightCB.setId("patientWeightUnitsEncounterEdit");
		weightCB.setHiddenName("patientWeightUnits");  
		weightCB.setStore(weightStore);  
		weightCB.setDisplayField("desc");  
		weightCB.setMode(ComboBox.LOCAL);  
		weightCB.setTriggerAction(ComboBox.ALL);  
		weightCB.setSelectOnFocus(true);  
		weightCB.setWidth(90); 
		weightCB.setName("patientWeightUnits");
		weightCB.setReadOnly(true);
		weightCB.setHideLabel(true);
		weightCB.setEditable(false);
		weightCB.setValueField("id");
		weightPanel.addToRow(weightCB, new ColumnLayoutData(2));
		if(!isEdit()){
			weightCB.setDisabled(true);
		}
	
		
		MultiFieldPanel heightPanel = new MultiFieldPanel();
		final NumberField patientHeight= new NumberField("Altura","patientHeight",90);
		patientHeight.setAllowNegative(false);
		patientHeight.setId("patientHeightEncounterEdit");
		if(!isEdit()){
			patientHeight.setReadOnly(true);
		}
		weightPanel.addToRow(patientHeight, 210);  
		Store heightStore = new SimpleStore(new String[]{"id", "desc"}, new String[][]{ new String[]{"Metros","Metros"}, 
				new String[]{"Pies", "Pies"}});  
		heightStore.load();  
		final ComboBox heightCB = new ComboBox();  
		heightCB.setId("patientHeightUnitsEncounterEdit");
		heightCB.setHiddenName("patientHeightUnits");  
		heightCB.setStore(heightStore);  
		heightCB.setDisplayField("desc"); 
		heightCB.setMode(ComboBox.LOCAL);  
		heightCB.setTriggerAction(ComboBox.ALL);  
		heightCB.setSelectOnFocus(true); 
		heightCB.setEditable(false);
		heightCB.setWidth(90); 
		heightCB.setName("patientHeightUnits");
		heightCB.setReadOnly(true);
		heightCB.setHideLabel(true);
		heightCB.setValueField("id");
		weightPanel.addToRow(heightCB, new ColumnLayoutData(2));
		if(!isEdit()){
			heightCB.setDisabled(true);
		}
		
		
		patientFS.add(weightPanel);
		patientFS.add(heightPanel);
		patientFS.add(toxicHabitsCB);
		patientFS.add(toxicHabitsDesc);
		
		
		FieldSet backgroundFS = new FieldSet("Antecedentes/Observaciones");  
		backgroundFS.setFrame(false);  
		backgroundFS.setAutoWidth(true);
		backgroundFS.setAutoHeight(true);
		backgroundFS.setCollapsible(true);
		backgroundFS.setAnimCollapse(true);
		
		final HtmlEditor backgroundEditor = new HtmlEditor(); 
		backgroundEditor.setName("background");
		backgroundEditor.setId("backgroundEditor");
		backgroundEditor.setHideLabel(true);
		backgroundEditor.setWidth(529);
		backgroundEditor.setHeight(268);  
		if(!isEdit()){
			backgroundEditor.setDisabled(true);
		}
		
		backgroundFS.add(backgroundEditor);
		
		
		/*Symptoms*/
		FieldDef[] gridFieldDefs = new FieldDef[] { 
				new StringFieldDef("id"),
				new StringFieldDef("name"),
				new StringFieldDef("type"),
				new StringFieldDef("htype"),
				new StringFieldDef("severity")
		};

		RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		HttpProxy gridProxy = null;
		if(isEdit()){
			 gridProxy = new HttpProxy("", Connection.GET);
			 
		}else{
			gridProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getEncounterRecords" +
					"&recordType=symptomOrDiseases&encounterId="+getEncounterId(), Connection.GET);
		}
		final Store gridStore = new Store(gridProxy,gridReader,true);
		if(!isEdit()){
			gridStore.load();
		}
		gridStore.setRemoteSort(false);
		   

		FieldSet symptomFS = new FieldSet("S&iacute;ntomas/Enfermedades");  
		symptomFS.setFrame(false);  
		symptomFS.setAutoWidth(true);
		symptomFS.setAutoHeight(true);
		symptomFS.setCollapsible(true);
		symptomFS.setAnimCollapse(true);
		
		ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
	      		new ColumnConfig("C&oacute;digo", "id"),
	            new ColumnConfig("Nombre", "name"),
	            new ColumnConfig("Tipo", "type"),
	            new ColumnConfig("Severidad", "severity")});
		 for (int i = 0; i < colModel.getColumnConfigs().length; i++){
	          ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
	      }
		
		symptomFS.add(getSymptomsGrid(gridStore, isEdit(), colModel, "S&iacute;ntomas/Enfermedades"));
			
		
		
		/*LabTests*/
		FieldDef[] gridFieldDefsL = new FieldDef[] { 
				new StringFieldDef("id"),
				new StringFieldDef("name"),
				new StringFieldDef("result"),
				new StringFieldDef("resultDesc"),
				new StringFieldDef("date")
		};

		RecordDef gridRecordDefL = new RecordDef(gridFieldDefsL);
		JsonReader gridReaderL = new JsonReader("response.value.items", gridRecordDefL);
		HttpProxy gridProxyL = null;
		if(isEdit()){
			gridProxyL = new HttpProxy("", Connection.GET);
			 
		}else{
			gridProxyL = new HttpProxy("/semrs/encounterServlet?encounterAction=getEncounterRecords" +
					"&recordType=labTests&encounterId="+getEncounterId(), Connection.GET);
		}
		final Store gridStoreL = new Store(gridProxyL,gridReaderL,true);
		if(!isEdit()){
			gridStoreL.load();
		}
		gridStoreL.setRemoteSort(false);
		   

		FieldSet labTestFS = new FieldSet("Ex&aacute;menes de Laboratorio");  
		labTestFS.setFrame(false);  
		labTestFS.setAutoWidth(true);
		labTestFS.setAutoHeight(true);
		labTestFS.setCollapsible(true);
		labTestFS.setAnimCollapse(true);
		
		ColumnModel colModelL = new ColumnModel(new ColumnConfig[] { 
	      		new ColumnConfig("C&oacute;digo", "id"),
	            new ColumnConfig("Nombre", "name"),
	            new ColumnConfig("Resultado", "result"),
	            new ColumnConfig("Observaciones", "resultDesc"),
	            new ColumnConfig("Fecha", "date")});
		 for (int i = 0; i < colModelL.getColumnConfigs().length; i++){
	          ((ColumnConfig) colModelL.getColumnConfigs()[i]).setSortable(true);
	      }
		
		 labTestFS.add(getLabTestGrid(gridStoreL, isEdit(), colModelL, "Ex&aacute;menes de Laboratorio"));

	
		 /*Complications*/
		 FieldDef[] gridFieldDefsC = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name"),
				 //new DateFieldDef("date","date","d/m/Y")
				 new StringFieldDef("date")
		 };

		 RecordDef gridRecordDefC = new RecordDef(gridFieldDefsC);
		 JsonReader gridReaderC = new JsonReader("response.value.items", gridRecordDefC);
		 HttpProxy gridProxyC = null;
			if(isEdit()){
				gridProxyC = new HttpProxy("", Connection.GET);
				 
			}else{
				gridProxyC = new HttpProxy("/semrs/encounterServlet?encounterAction=getEncounterRecords" +
						"&recordType=complications&encounterId="+getEncounterId(), Connection.GET);
			}
			final Store gridStoreC = new Store(gridProxyC,gridReaderC,true);
			if(!isEdit()){
				gridStoreC.load();
			}
		 gridStoreC.setRemoteSort(false);


		 FieldSet complicationFS = new FieldSet("Complicaciones");  
		 complicationFS.setFrame(false);  
		 complicationFS.setAutoWidth(true);
		 complicationFS.setAutoHeight(true);
		 complicationFS.setCollapsible(true);
		 complicationFS.setAnimCollapse(true);

		 ColumnModel colModelC = new ColumnModel(new ColumnConfig[] { 
				 new ColumnConfig("C&oacute;digo", "id"),
				 new ColumnConfig("Nombre", "name"),
				 new ColumnConfig("Fecha", "date")});
		 for (int i = 0; i < colModelC.getColumnConfigs().length; i++){
			 ((ColumnConfig) colModelC.getColumnConfigs()[i]).setSortable(true);
		 }

		 complicationFS.add(getComplicationGrid(gridStoreC, isEdit(), colModelC, "Complicaciones"));

		
		 /*Procedures*/
		 FieldDef[] gridFieldDefsP = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name"),
				 //new DateFieldDef("date","date","d/m/Y")
				 new StringFieldDef("date")
		 };

		 RecordDef gridRecordDefP = new RecordDef(gridFieldDefsP);
		 JsonReader gridReaderP = new JsonReader("response.value.items", gridRecordDefP);
		 HttpProxy gridProxyP = null;
			if(isEdit()){
				gridProxyP = new HttpProxy("", Connection.GET);
				 
			}else{
				gridProxyP = new HttpProxy("/semrs/encounterServlet?encounterAction=getEncounterRecords" +
						"&recordType=procedures&encounterId="+getEncounterId(), Connection.GET);
			}
			final Store gridStoreP = new Store(gridProxyP,gridReaderP,true);
			if(!isEdit()){
				gridStoreP.load();
			}
		 gridStoreP.setRemoteSort(false);


		 FieldSet proceduresFS = new FieldSet("Procedimientos");  
		 proceduresFS.setFrame(false);  
		 proceduresFS.setAutoWidth(true);
		 proceduresFS.setAutoHeight(true);
		 proceduresFS.setCollapsible(true);
		 proceduresFS.setAnimCollapse(true);

		 ColumnModel colModelP = new ColumnModel(new ColumnConfig[] { 
				 new ColumnConfig("C&oacute;digo", "id"),
				 new ColumnConfig("Nombre", "name"),
				 new ColumnConfig("Fecha", "date")});
		 for (int i = 0; i < colModelP.getColumnConfigs().length; i++){
			 ((ColumnConfig) colModelP.getColumnConfigs()[i]).setSortable(true);
		 }

		 proceduresFS.add(getProcedureGrid(gridStoreP, isEdit(), colModelP, "Procedimientos"));
		 
		 /*Diagnosis*/
		 FieldDef[] gridFieldDefsDiag = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name"),
				 new StringFieldDef("severity"),
				 new StringFieldDef("type")
		 };

		 RecordDef gridRecordDefDiag = new RecordDef(gridFieldDefsDiag);
		 JsonReader gridReaderDiag = new JsonReader("response.value.items", gridRecordDefDiag);
		 HttpProxy gridProxyDiag = null;
			if(isEdit()){
				gridProxyDiag = new HttpProxy("", Connection.GET);
				 
			}else{
				gridProxyDiag = new HttpProxy("/semrs/encounterServlet?encounterAction=getEncounterRecords" +
						"&recordType=diagnoses&encounterId="+getEncounterId(), Connection.GET);
			}
			 final Store gridStoreDiag = new Store(gridProxyDiag,gridReaderDiag,true);
			if(!isEdit()){
				gridStoreDiag.load();
			}
		 gridStoreDiag.setRemoteSort(false);


		 FieldSet diagnosesFS = new FieldSet("Diagnosticos");  
		 diagnosesFS.setFrame(false);  
		 diagnosesFS.setAutoWidth(true);
		 diagnosesFS.setAutoHeight(true);
		 diagnosesFS.setCollapsible(true);
		 diagnosesFS.setAnimCollapse(true);

		 ColumnModel colModelDiag = new ColumnModel(new ColumnConfig[] { 
				 new ColumnConfig("C&oacute;digo", "id"),
				 new ColumnConfig("Nombre", "name"),
				 new ColumnConfig("Severidad", "severity"),
				 new ColumnConfig("Tipo", "type")});
		 for (int i = 0; i < colModelDiag.getColumnConfigs().length; i++){
			 ((ColumnConfig) colModelDiag.getColumnConfigs()[i]).setSortable(true);
		 }

		 diagnosesFS.add(getDiagnosisGrid(gridStoreDiag, isEdit(), colModelDiag, "Diagnosticos"));
		 
		 
		 /*Drugs*/
		 FieldDef[] gridFieldDefsDrugs = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name"),
				// new DateFieldDef("startDate","startDate","d/m/Y"),
				// new DateFieldDef("endDate","endDate","d/m/Y"),
				 new StringFieldDef("startDate"),
			     new StringFieldDef("endDate"),
				 new StringFieldDef("instructions")
		 };

		 RecordDef gridRecordDefDrugs = new RecordDef(gridFieldDefsDrugs);
		 JsonReader gridReaderDrugs = new JsonReader("response.value.items", gridRecordDefDrugs);
		 HttpProxy gridProxyDrugs = null;
			if(isEdit()){
				gridProxyDrugs = new HttpProxy("", Connection.GET);
				 
			}else{
				gridProxyDrugs = new HttpProxy("/semrs/encounterServlet?encounterAction=getEncounterRecords" +
						"&recordType=treatments&encounterId="+getEncounterId(), Connection.GET);
			}
			final Store gridStoreDrugs = new Store(gridProxyDrugs,gridReaderDrugs,true);
			if(!isEdit()){
				gridStoreDrugs.load();
			}

		 gridStoreDrugs.setRemoteSort(false);


		 FieldSet drugsFS = new FieldSet("Tratamientos");  
		 drugsFS.setFrame(false);  
		 drugsFS.setAutoWidth(true);
		 drugsFS.setAutoHeight(true);
		 drugsFS.setCollapsible(true);
		 drugsFS.setAnimCollapse(true);

		 ColumnModel colModelDrugs = new ColumnModel(new ColumnConfig[] { 
				 new ColumnConfig("C&oacute;digo", "id"),
				 new ColumnConfig("Nombre", "name"),
				 new ColumnConfig("Fecha Inicio", "startDate"),
				 new ColumnConfig("Fecha Fin", "endDate"),
				 new ColumnConfig("Instrucciones", "instructions")});
		 for (int i = 0; i < colModelDrugs.getColumnConfigs().length; i++){
			 ((ColumnConfig) colModelDrugs.getColumnConfigs()[i]).setSortable(true);
		 }

		 drugsFS.add(getDrugGrid(gridStoreDrugs, isEdit(), colModelDrugs, "Tratamientos"));
		


		
		Panel firstColumn = new Panel();  
	    firstColumn.setLayout(new FormLayout());  
	    firstColumn.setBorder(true);
	    firstColumn.setFrame(true);
	    
		
		firstColumn.add(patientFS,new AnchorLayoutData("100%") );
		firstColumn.add(symptomFS,new AnchorLayoutData("100%") );  
		firstColumn.add(proceduresFS,new AnchorLayoutData("100%") );
		firstColumn.add(diagnosesFS,new AnchorLayoutData("100%") );
		
		Panel secondColumn = new Panel();  
		secondColumn.setLayout(new FormLayout());  
		secondColumn.setBorder(true); 
		secondColumn.setFrame(true);

		secondColumn.add(backgroundFS,new AnchorLayoutData("100%") );
		secondColumn.add(labTestFS,new AnchorLayoutData("100%") );
		secondColumn.add(complicationFS,new AnchorLayoutData("100%") );
		secondColumn.add(drugsFS,new AnchorLayoutData("100%") );
		
		Panel columnPanel = new Panel();
		columnPanel.setLayout(new ColumnLayout());
		columnPanel.setButtonAlign(Position.CENTER);

		columnPanel.add(firstColumn, new ColumnLayoutData(0.5));
		columnPanel.add(secondColumn, new ColumnLayoutData(0.5));
		
		final FieldDef[] diagnosisFieldDefs = new FieldDef[] { 
				new StringFieldDef("id"),
				new StringFieldDef("name"),
				new FloatFieldDef("probability")
				//new IntegerFieldDef("probabilityInt"),
		};

		final RecordDef diagnosisRecordDef = new RecordDef(diagnosisFieldDefs);
		final JsonReader diagnosisGridReader = new JsonReader("response.value.items", diagnosisRecordDef);
		
		
		
		

		formStore.addStoreListener(new StoreListenerAdapter(){

			public void onLoad(Store store, Record[] records) {

				for(int i=0;i<records.length;i++){
					if(store.getRecordAt(i).getAsString("loadSuccess").equals("false")){
						MessageBox.show(new MessageBoxConfig() {  
        					{  
        						setTitle("Error");
        						setMsg("Esta consulta no existe");
        						setIconCls(MessageBox.ERROR);
        					    setModal(true);
        					    setButtons(MessageBox.OK);
        					    setCallback(new MessageBox.PromptCallback() { 
								public void execute(
										String btnID,
										String text) {
									getCurrentEncountersStore().reload();
									if(getPreviousEncountersStore()!=null){
										 getPreviousEncountersStore().load();
										}
									MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
									
								}  
                                });  
        					}  
        				});
						break;
					}else if(store.getRecordAt(i).getAsString("editSuccess").equals("false")){
						MessageBox.show(new MessageBoxConfig() {  
        					{  
        						setTitle("Error");
        						setMsg("Esta consulta no puede ser modificada");
        						setIconCls(MessageBox.ERROR);
        					    setModal(true);
        					    setButtons(MessageBox.OK);
        					    setCallback(new MessageBox.PromptCallback() { 
								public void execute(
										String btnID,
										String text) {
									getCurrentEncountersStore().reload();
									if(getPreviousEncountersStore()!=null){
										 getPreviousEncountersStore().load();
										}
									MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
									
								}  
                                });  
        					}  
        				});
						break;
						
					}else if(store.getRecordAt(i).getAsString("loadPatientSuccess").equals("false")){
						MessageBox.show(new MessageBoxConfig() {  
        					{  
        						setTitle("Error");
        						setMsg("Esta paciente no existe o ha sido dado de alta");
        						setIconCls(MessageBox.ERROR);
        					    setModal(true);
        					    setButtons(MessageBox.OK);
        					    setCallback(new MessageBox.PromptCallback() { 
								public void execute(
										String btnID,
										String text) {
									AdminUsersScreen.reloadFlag = true;
									MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
									
								}  
                                });  
        					}  
        				});
						break;
						
					}else{
						encounterIdText.setValue(store.getRecordAt(i).getAsString("id"));
						patientIdText.setValue(store.getRecordAt(i).getAsString("patientId"));
						patientNameText.setValue(store.getRecordAt(i).getAsString("patientName"));
						patientLastNameText.setValue(store.getRecordAt(i).getAsString("patientLastName"));
						patientAgeText.setValue(store.getRecordAt(i).getAsString("patientAge"));
						patientSexText.setValue(store.getRecordAt(i).getAsString("patientSex"));
						toxicHabitsDesc.setValue(store.getRecordAt(i).getAsString("toxicHabitsDesc"));
						toxicHabitsCB.setValue(store.getRecordAt(i).getAsString("toxicHabits"));
						if(store.getRecordAt(i).getAsString("toxicHabits").equals("Si")){
							toxicHabitsDesc.setVisible(true);
						}
						encounterRefferral.setValue(store.getRecordAt(i).getAsString("encounterRefferral"));
						encounterReason.setValue(store.getRecordAt(i).getAsString("encounterReason"));
						patientWeight.setValue(store.getRecordAt(i).getAsString("patientWeight"));
						weightCB.setValue(store.getRecordAt(i).getAsString("patientWeightUnits"));
						patientHeight.setValue(store.getRecordAt(i).getAsString("patientHeight"));
						heightCB.setValue(store.getRecordAt(i).getAsString("patientHeightUnits"));
						patientAgeHidden.setValue(store.getRecordAt(i).getAsString("patientAgeH"));
						backgroundEditor.setValue(store.getRecordAt(i).getAsString("background"));
					
						
					}
				}


			}
			public void onLoadException(Throwable error) {
				MessageBox.show(new MessageBoxConfig() {  
					{  
						setTitle("Error");
						setMsg("Ocurrio un error al tratar de obtener esta consulta");
						setIconCls(MessageBox.ERROR);
					    setModal(true);
					    setButtons(MessageBox.OK);
					    setCallback(new MessageBox.PromptCallback() { 
						public void execute(
								String btnID,
								String text) {
							MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
							
						}  
                        });  
					}  
				});

		}});	
		
		
	if(isEdit()){
		
	     final Button saveButton = new Button("Guardar");
			saveButton.setId("saveEncounterEditButton");
			saveButton.addListener(new ButtonListenerAdapter(){
				public void onClick(final Button button, EventObject e){
					if(gridStoreDiag.getRecords().length <=0){
						
						MessageBox.show(new MessageBoxConfig() {  
							{  
								setTitle("Error");
								setMsg("Por favor introduzca al menos 1 diagnostico para continuar.");
								setIconCls(MessageBox.ERROR);
							    setModal(true);
							    setButtons(MessageBox.OK); 
							}  
						});
						
					}else{
			     ExtendedMessageBox.confirmlg("Confirmar","Al guardar los cambios esta consulta no podra ser modificada, Esta seguro que desea continuar?", "Si", "No",  
	               new MessageBox.ConfirmCallback() {  
	                   public void execute(String btnID) {
	                      if(btnID.equals("yes")){

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
			
						RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,  "/semrs/encounterServlet?encounterAction=saveEncounter"+
								"&"+formPanel.getForm().getValues()+
								"&symptomsDiseases="+getRecordValues(gridStore.getRecords(),true,false,false,false,false)+
								"&labTests="+getRecordValues(gridStoreL.getRecords(),false,true,false,false,false)+
								"&procedures="+getRecordValues(gridStoreP.getRecords(),false,false,true,false,false)+
								"&complications="+getRecordValues(gridStoreC.getRecords(),false,false,true,false,false)+
								"&diagnoses="+getRecordValues(gridStoreDiag.getRecords(),false,false,false,false,true)+
								"&treatments="+getRecordValues(gridStoreDrugs.getRecords(),false,false,false,true,false));
						try {

							rb.sendRequest(null, new RequestCallback() {
								public void onResponseReceived(Request req, final Response res) {
									//MessageBox.hide();  
			                        //MessageBox.getDialog().close();
									if(res.getText().indexOf("errores") !=-1){
										MessageBox.hide();  
				                        MessageBox.getDialog().close();
										MessageBox.show(new MessageBoxConfig() {  
				        					{  
				        						setTitle("Error");
				        						setMsg(res.getText());
				        						setIconCls(MessageBox.ERROR);
				        					    setModal(true);
				        					    setButtons(MessageBox.OK);
				        					}  
				        				});
									
									}else if(res.getText().equals("")){
										MessageBox.hide();  
										MessageBox.getDialog().close();
										MessageBox.hide();
										MessageBox.alert("Error", "Error interno"); 
									}else{  	
										MainPanel.resetTimer();
										ListPatientsScreen.reloadFlag = true;
										ListEncountersScreen.reloadFlag =true;
										getCurrentEncountersStore().load();
										final Timer timer = new Timer() {
											public void run() {
												MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
											}
										};
										final Timer timer2 = new Timer() {
											public void run() {
												if(getPreviousEncountersStore()!=null){
												 getPreviousEncountersStore().load();
												}
												MessageBox.hide();  
												MessageBox.getDialog().close();
												MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText(), new MessageBox.AlertCallback() {  
													public void execute() {

														timer.schedule(2000); 


													}});
											}
										};

										timer2.schedule(5000); 


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
			 }
			});
			saveButton.setIconCls("save-icon");
			proxyPanel.addButton(saveButton);
		
	
		
		final Button deleteEncounterButton = new Button("Eliminar");
		deleteEncounterButton.setId("deleteEncounterEditButton");
		deleteEncounterButton.addListener(new ButtonListenerAdapter() {  
			public void onClick(final Button button, EventObject e) {
					ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar esta consulta?", "Si", "No",  
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

								final RequestBuilder deleteRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/encounterServlet?encounterAction=deleteEncounter&encounterId="+getEncounterId());
								try {

									deleteRequest.sendRequest(null, new RequestCallback() {
										public void onResponseReceived(Request req, final Response res) {
											MessageBox.hide();  
											if(res.getText().indexOf("errores") !=-1){
												MessageBox.show(new MessageBoxConfig() {  
						        					{  
						        						setTitle("Error");
						        						setMsg(res.getText());
						        						setIconCls(MessageBox.ERROR);
						        					    setModal(true);
						        					    setButtons(MessageBox.OK);
						        					}  
						        				}); 
											}else if(res.getText().equals("")){
												MessageBox.hide();
												MessageBox.alert("Error", "Error interno"); 
											}else{
												getCurrentEncountersStore().reload();
												ListEncountersScreen.reloadFlag =true;
												MainPanel.resetTimer();
												MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());  
												MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
											}


										}

										public void onError(Request req, Throwable exception) {
											MessageBox.hide();
											MessageBox.alert("Error", "Error interno"); 
										}

									});

								} catch (RequestException re) {
									MessageBox.hide();
									MessageBox.alert("Error", "Error interno"); 

								}
							}
						}  
					});  
			}  
		});  
		deleteEncounterButton.setIconCls("delete-icon");
		proxyPanel.addButton(deleteEncounterButton);
		
		
		
		final Button diagnosisButton = new Button("Diagn&oacute;stico Diferencial Asistido");
		diagnosisButton.setId("diagnosisEncounterDetailButton");
		diagnosisButton.addListener(new ButtonListenerAdapter(){
        	public void onClick(final Button button, EventObject e){
        	
        				if(gridStore.getRecords().length <=0 && gridStoreL.getRecords().length<=0){
    						
    						MessageBox.show(new MessageBoxConfig() {  
    							{  
    								setTitle("Error");
    								setMsg("Por favor introduzca al menos 1 s&iacute;ntoma o ex&aacute;men de laboratorio para continuar.");
    								setIconCls(MessageBox.ERROR);
    							    setModal(true);
    							    setButtons(MessageBox.OK); 
    							}  
    						});
    						
    					}else{
        		
        		
        		HttpProxy diagnosisGridProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getDiagnosis"+
        				"&"+formPanel.getForm().getValues()+
        				"&symptomsDiseases="+getRecordValues(gridStore.getRecords(),true,false,false,false,false)+
        				"&labTests="+getRecordValues(gridStoreL.getRecords(),false,true,false,false,false)+
        				"&procedures="+getRecordValues(gridStoreP.getRecords(),false,false,true,false,false)+
        				"&complications="+getRecordValues(gridStoreC.getRecords(),false,false,true,false,false)+
        				"&treatments="+getRecordValues(gridStoreDrugs.getRecords(),false,false,false,true,false), Connection.GET);

        		final Store diagnosisGridStore = new Store(diagnosisGridProxy,diagnosisGridReader,false);
        		
        		HttpProxy diagnosisPredictionGridProxy = new HttpProxy("/semrs/encounterServlet?encounterAction=getDiagnosis&getPrediction=true"+
        				"&"+formPanel.getForm().getValues()+
        				"&symptomsDiseases="+getRecordValues(gridStore.getRecords(),true,false,false,false,false)+
        				"&labTests="+getRecordValues(gridStoreL.getRecords(),false,true,false,false,false)+
        				"&procedures="+getRecordValues(gridStoreP.getRecords(),false,false,true,false,false)+
        				"&complications="+getRecordValues(gridStoreC.getRecords(),false,false,true,false,false)+
        				"&treatments="+getRecordValues(gridStoreDrugs.getRecords(),false,false,false,true,false), Connection.GET);

        		final Store diagnosisPredictionGridStore = new Store(diagnosisPredictionGridProxy,diagnosisGridReader,false);
        		
        		MainPanel.resetTimer();
        		
        		//button.setDisabled(true);	
     
       		 MessageBox.show(new MessageBoxConfig() {  
				 {  
					 setMsg("Realizando Diagn&oacute;stico, por favor espere...");  
					 setProgressText("Analizando Datos...");  
					 setWidth(300);  
					 setWait(true);  
					 setWaitConfig(new WaitConfig() {  
						 {  
							 setInterval(200);  
						 }  
					 });  
					 setAnimEl(diagnosisButton.getId());  
				 }  
			 }); 
       		 
       		 
       		 diagnosisPredictionGridStore.addStoreListener(new StoreListenerAdapter(){
       			 public void onLoad(Store store, Record[] records) {

       				 store.add(records);
       				 MessageBox.hide();  
       				 MessageBox.getDialog().close();
       				 if(records.length<=0 && diagnosisGridStore.getRecords().length<=0){
       					 MessageBox.show(new MessageBoxConfig() {  
       						 {  
       							 setTitle("Atenci&oacute;n");
       							 setMsg("No se encontraron resultados.");
       							 setIconCls(MessageBox.INFO);
       							 setModal(true);
       							 setButtons(MessageBox.OK);
       						 }  
       					 });

       					// button.setDisabled(false);
       				 }else{
       					 Window window = getDiagnosisGridWindow(diagnosisGridStore,store);
       					 window.addListener(new PanelListenerAdapter(){
       						 public void onClose(Panel panel) {
       							 //button.setDisabled(false);
       						 }
       						 public void onDeactivate(Panel panel) {
       							 //button.setDisabled(false);
       						 }


       					 });
       					 window.show();
       				 }

       			 }

       			 public void onLoadException(Throwable error) {
       				 MessageBox.hide();  
       				 MessageBox.getDialog().close();
       				 MessageBox.show(new MessageBoxConfig() {  
       					 {  
       						 setTitle("Error");
       						 setMsg("Ha ocurrido un error al tratar de realizar el diagn&oacute;stico.");
       						 setIconCls(MessageBox.ERROR);
       						 setModal(true);
       						 setButtons(MessageBox.OK);
       					 }  
       				 });

       			 }


       		 });




       		 diagnosisGridStore.addStoreListener(new StoreListenerAdapter(){
       			 public void onLoad(Store store, Record[] records) {

       				 store.add(records);
       				 //  MessageBox.hide();  
       				 // MessageBox.getDialog().close();		 
       				 diagnosisPredictionGridStore.load();

       			 }

       			 public void onLoadException(Throwable error) {
       				 MessageBox.hide();  
       				 MessageBox.getDialog().close();
       				 MessageBox.show(new MessageBoxConfig() {  
       					 {  
       						 setTitle("Error");
       						 setMsg("Ha ocurrido un error al tratar de realizar el diagn&oacute;stico.");
       						 setIconCls(MessageBox.ERROR);
       						 setModal(true);
       						 setButtons(MessageBox.OK);
       					 }  
       				 });
       				// button.setDisabled(false);
       			 }


       		 });

       		
       		diagnosisGridStore.load();
       		 
    					}
        	}
        	
        });
		diagnosisButton.setIconCls("disease-icon");
		proxyPanel.addButton(diagnosisButton);
		
		
		
	
	}else{
		
		Button report = new Button("Exportar");
		report.setId("reportEncounterDetailButton");
		report.addListener(new ButtonListenerAdapter(){
        	public void onClick(Button button, EventObject e){
        		com.google.gwt.user.client.Window.open("/semrs/encounterServlet?encounterAction=getEncounterReport&encounterId="+getEncounterId(), "_self", ""); 
        	}
        	
        });
		report.setIconCls("acrobat-icon");
		proxyPanel.addButton(report);
		
	}
		
		Button cancel = new Button("Cancelar");
		cancel.setId("cancelEncounterDetailButton");
        cancel.addListener(new ButtonListenerAdapter(){
        	public void onClick(Button button, EventObject e){
        		MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
        	}
        	
        });
        cancel.setIconCls("cancel-icon");
		proxyPanel.addButton(cancel);
		
		
		
		
		formPanel.add(columnPanel);
		formPanel.add(proxyPanel);
		formPanel.doLayout();
		panel.add(formPanel);
		panel.doLayout();
		return panel;
	}

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Store getCurrentEncountersStore() {
		return currentEncountersStore;
	}

	public void setCurrentEncountersStore(Store currentEncountersStore) {
		this.currentEncountersStore = currentEncountersStore;
	}

	public Store getPreviousEncountersStore() {
		return previousEncountersStore;
	}

	public void setPreviousEncountersStore(Store previousEncountersStore) {
		this.previousEncountersStore = previousEncountersStore;
	}


}
