package com.googlecode.semrs.client.screens.patient;

import java.util.Date;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.googlecode.semrs.client.ExtendedMessageBox;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
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
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.TimeField;
import com.gwtext.client.widgets.form.event.CheckboxListenerAdapter;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowParams;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;

public class ListEncountersScreen  extends ShowcasePanel{



	private com.gwtext.client.widgets.TabPanel tabPanel;

	public static boolean reloadFlag = false;

	FieldDef[] fieldDefs = new FieldDef[] { 
			new StringFieldDef("id"),
			new StringFieldDef("patientId"), 
			new StringFieldDef("patientName"), 
			new StringFieldDef("encounterProvider"), 
			new StringFieldDef("encounterDate"),
			new StringFieldDef("refferral"),
			new StringFieldDef("reason"),
			new StringFieldDef("creationDate"),	
			new StringFieldDef("creationUser"),
			new StringFieldDef("edit"),
			new StringFieldDef("endDate"),
			new StringFieldDef("finalized"),
			new StringFieldDef("lastEditDate"),
			new StringFieldDef("lastEditUser")
	};

	RecordDef recordDef = new RecordDef(fieldDefs);

	JsonReader reader = new JsonReader("response.value.items", recordDef);

	HttpProxy proxy = new HttpProxy("/semrs/encounterServlet?encounterAction=listEncounters", Connection.GET);
	final Store store = new Store(proxy,reader,true);
	final PagingToolbar pagingToolbar = new PagingToolbar(store);





	public ListEncountersScreen(){
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



			store.setDefaultSort("encounterDate", SortDir.DESC);
			store.addStoreListener(new StoreListenerAdapter() {
				public void onLoadException(Throwable error) {
					//Check for session expiration
					RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
					try {
						rb.sendRequest(null, new RequestCallback() {

							public void onError(Request request, Throwable exception) {
								MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de consultas.");
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
									MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de consultas.");
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
			formPanel.setTitle("Administrar Consultas");  
			formPanel.setWidth(900);  
			formPanel.setLabelWidth(100); 
			formPanel.setPaddings(5);  
			formPanel.setLabelAlign(Position.TOP);
			formPanel.setIconCls("encounter-icon");

			 

			TextField patientId = new TextField("N&uacute;mero de C&eacute;dula", "patientId");
			patientId.setStyle("textTransform: uppercase;");
			patientId.addListener(new FieldListenerAdapter(){
				public void onBlur(Field field) {
					String value = field.getValueAsString();
					field.setValue(value.toUpperCase());
				}

			});
			
			
			 final Hidden providerIdEncounter = new Hidden();
		     providerIdEncounter.setName("encounterProvider");
		     
		     FieldDef[] cbFieldDefs = new FieldDef[] { 
		    		 new StringFieldDef("username"),
		    		 new StringFieldDef("name")
		     };

		     RecordDef cbRecordDef = new RecordDef(cbFieldDefs);

		     JsonReader cbReader = new JsonReader("response.value.items", cbRecordDef);
		     
		     HttpProxy cbProxy = new HttpProxy("/semrs/patientServlet?patientAction=getProviders", Connection.GET);
		     final Store cbStore = new Store(cbProxy,cbReader,true);
		     cbStore.load();
		     
		     final ComboBox providerCB = new ComboBox();  
		     providerCB.setName("providerIdCB2");
		     providerCB.setMinChars(1);  
		     providerCB.setFieldLabel("M&eacute;dico Tratante");  
		     providerCB.setStore(cbStore);
		     providerCB.setDisplayField("name");  
		     providerCB.setValueField("username");
		     providerCB.setMode(ComboBox.REMOTE);  
		     providerCB.setTriggerAction(ComboBox.ALL);  
		     providerCB.setEmptyText(" ");  
		     providerCB.setLoadingText("Buscando...");  
		     providerCB.setTypeAhead(true);  
		     providerCB.setSelectOnFocus(true);  
		     providerCB.setWidth(200);  
		     providerCB.setPageSize(10);
		     providerCB.addListener(new ComboBoxListenerAdapter(){
		         public void onSelect(ComboBox comboBox, Record record, int index){
		        	 providerIdEncounter.setValue(record.getAsString("username"));
		         }
		       });

				final DateField encounterDateFrom = new DateField("Fecha de Consulta Desde", "encounterDateFrom", 190);
				encounterDateFrom.setReadOnly(true);

				final DateField encounterDateTo   = new DateField("Fecha de Consulta Hasta", "encounterDateTo", 190);
				encounterDateTo.setReadOnly(true);


				final DateField creationDateFrom = new DateField("Fecha de Creaci&oacute;n Desde", "creationDateFrom", 190);
				creationDateFrom.setReadOnly(true);
				//creationDateFrom.setMaxValue(new Date());

				final DateField creationDateTo   = new DateField("Fecha de Creaci&oacute;n Hasta", "creationDateTo", 190);
				creationDateTo.setReadOnly(true);
				//creationDateTo.setMaxValue(new Date());
				
				final DateField endDateFrom = new DateField("Fecha F&iacute;n Desde", "endDateFrom", 190);  
				endDateFrom.setReadOnly(true);

				final DateField endDateTo = new DateField("Fecha F&iacute;n Hasta", "endDateTo", 190);  
				endDateTo.setReadOnly(true);
				
				final TextField current = new TextField("current","current",190);
				current.setVisible(false); 
				Checkbox currentCb = new Checkbox("Incluir Consultas Pasadas"); 
				currentCb.addListener(new CheckboxListenerAdapter() {  
					public void onCheck(Checkbox field, boolean checked) {  

						if (checked) {  
							current.setValue("optional");
						} else {  
							current.setValue("true");
						}  
					}  
				});         
		

				
			Panel topPanel = new Panel();  
			topPanel.setLayout(new ColumnLayout());  
			topPanel.setBorder(false);  
		
			Panel columnOnePanel = new Panel();  
			columnOnePanel.setLayout(new FormLayout()); 	
		    columnOnePanel.add(patientId, new AnchorLayoutData("25%"));  
		    columnOnePanel.add(encounterDateFrom, new AnchorLayoutData("25%"));
		    columnOnePanel.add(encounterDateTo, new AnchorLayoutData("25%"));
		    columnOnePanel.add(endDateFrom, new AnchorLayoutData("25%"));
		    columnOnePanel.add(endDateTo, new AnchorLayoutData("25%"));
			topPanel.add(columnOnePanel, new ColumnLayoutData(.5));  

	
			Panel columnTwoPanel = new Panel();  
			columnTwoPanel.setLayout(new FormLayout());  
			columnTwoPanel.add(providerCB, new AnchorLayoutData("55%"));  
			columnTwoPanel.add(providerIdEncounter, new AnchorLayoutData("25%"));  
			columnTwoPanel.add(creationDateFrom, new AnchorLayoutData("25%"));  
			columnTwoPanel.add(creationDateTo, new AnchorLayoutData("25%"));  
			columnTwoPanel.add(currentCb, new AnchorLayoutData("65%"));  
			columnTwoPanel.add(current, new AnchorLayoutData("65%"));  
			topPanel.add(columnTwoPanel, new ColumnLayoutData(0.5));  

  

			FieldSet fieldSet = new FieldSet();
			fieldSet.add(topPanel);
			fieldSet.setTitle("B&uacute;squeda de Consultas");
			fieldSet.setCollapsible(true);
			fieldSet.setAnimCollapse(true);
			fieldSet.setCollapsed(true);
			fieldSet.setFrame(false);


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
	            		UrlParam[] formParams = getFormData(formPanel.getForm());
	            		store.setBaseParams(formParams);
	            		store.load(0, pagingToolbar.getPageSize());
	            		pagingToolbar.updateInfo();
	            		MainPanel.resetTimer();
	            	}
	            	
	            });
	            proxyPanel.addButton(search);  
	            fieldSet.add(proxyPanel);
	            formPanel.add(fieldSet);  
	            
	            formPanel.setMonitorValid(true);
	            formPanel.addListener(new FormPanelListenerAdapter() {
	                public void onClientValidation(FormPanel formPanel, boolean valid) {
	                	search.setDisabled(!valid);
	                }
	            });
 
              
	            GridView encountersView = new GridView(){
	          	   public String getRowClass(Record record, int index, RowParams rowParams, Store store) {
	          		   if(record.getAsString("edit").startsWith("f")){
	          			   return "redClass";
	          		   }
	          		   return "";
	                 }
	             };
	             encountersView.setEmptyText("No hay Registros");
	             encountersView.setAutoFill(true);
	             encountersView.setForceFit(true);

	             
	             final ToolbarButton deleteEncounterButton = new ToolbarButton("Eliminar Consulta");
	             deleteEncounterButton.setIconCls("delete-icon");
	             deleteEncounterButton.setDisabled(true); 
	             
	             final GridPanel encountersGrid = new GridPanel(store, createColModel());
	             encountersGrid.setEnableDragDrop(false);
	             encountersGrid.setWidth(920);
	             encountersGrid.setHeight(550);
	             encountersGrid.setTitle("Lista de Consultas");
	             encountersGrid.setLoadMask(true);  
	             encountersGrid.setSelectionModel(new RowSelectionModel());  
	             encountersGrid.setFrame(true);  
	             encountersGrid.setView(encountersView);
	             encountersGrid.addGridCellListener(new GridCellListener() {  

	     			
	     			public void onCellDblClick(GridPanel grid, int rowIndex,
	     					int colIndex, EventObject e) {
	     			    Record r = grid.getStore().getAt(rowIndex);
	     			    String edit = r.getAsString("edit");
	     			    String finalized = r.getAsString("finalized");
	     			    ShowcasePanel.adminEncounterScreen.setEncounterId(r.getAsString("id"));
	     			    ShowcasePanel.adminEncounterScreen.flag1 = false;
	     			    if(edit.startsWith("t")){
	     			    	ShowcasePanel.adminEncounterScreen.setEdit(true);
	     			    	ShowcasePanel.adminEncounterScreen.setCurrentEncountersStore(store);
	     			    	showScreen(getTabPanel(),ShowcasePanel.adminEncounterScreen, "Editar Consulta","encounter-icon","admEncounterDetail");
	     			    }else if(edit.startsWith("f") && finalized.equals("false")){
	     			    	 MessageBox.show(new MessageBoxConfig() {  
	          					{  
	          						setTitle("Atenci&oacute;n");
	          						setMsg("No se encuentra autorizado para editar esta consulta");
	          						setIconCls(MessageBox.WARNING);
	          					    setModal(true);
	          					    setButtons(MessageBox.OK); 
	          					}  
	          				});
	     			    }else if(edit.startsWith("f") && finalized.equals("true")){
	     			    	ShowcasePanel.adminEncounterScreen.setEdit(false);
	     			    	ShowcasePanel.adminEncounterScreen.setCurrentEncountersStore(store);
	     			    	showScreen(getTabPanel(),ShowcasePanel.adminEncounterScreen, "Editar Consulta","encounter-icon","admEncounterDetail");
	     			    }
	     			}

	     			
	     			public void onCellClick(GridPanel grid, int rowIndex,
	     					int colIndex, EventObject e) {
	     		      
	     				final Record r = grid.getStore().getAt(rowIndex);
	     				String recordId = r.getAsString("id");
	     				deleteEncounterButton.setDisabled(false);
	     				deleteEncounterButton.setId(recordId);

	     				
	     			}


	     			public void onCellContextMenu(GridPanel grid, int rowIndex,
	     					int cellIndex, EventObject e) {
	     				// TODO Auto-generated method stub
	     				
	     			}
	                 });  
	             
	            
	             pagingToolbar.setPageSize(10);
	             pagingToolbar.setDisplayInfo(true);
	             pagingToolbar.setEmptyMsg("No hay registros");
	       

	             NumberField encountersPageSizeField = new NumberField();
	             //currentEncountersPageSizeField.setId("currentEncountersPageSizeField");
	             encountersPageSizeField.setWidth(40);
	             encountersPageSizeField.setSelectOnFocus(true);
	             encountersPageSizeField.addListener(new FieldListenerAdapter() {
	                 public void onSpecialKey(Field field, EventObject e) {
	                     if (e.getKey() == EventObject.ENTER) {
	                         int pageSize = Integer.parseInt(field.getValueAsString());
	                         pagingToolbar.setPageSize(pageSize);
	                     }
	                 }
	             });

	             ToolTip encountersTooltip = new ToolTip("Introduzca el tama&ntilde;o de p&aacute;gina");
	             encountersTooltip.applyTo(encountersPageSizeField);
	             
	             pagingToolbar.addField(encountersPageSizeField);
	             pagingToolbar.addSeparator();
	             final ToolbarButton addEncounterButton = new ToolbarButton("Nueva Consulta");
	             addEncounterButton.setIconCls("add-icon");
	             addEncounterButton.addListener(new ButtonListenerAdapter() {  
		     			public void onClick(final Button button, EventObject e) {
		     				 getNewEncounterWindow().show();
		     			}
	             });
	            
	             pagingToolbar.addButton(addEncounterButton);
	             pagingToolbar.addSeparator();
	     		 deleteEncounterButton.addListener(new ButtonListenerAdapter() {  
	     			public void onClick(final Button button, EventObject e) {
	     				if(!button.getId().equals("")){
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

	     								final RequestBuilder deleteRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/encounterServlet?encounterAction=deleteEncounter&encounterId="+button.getId());
	     								try {

	     									deleteRequest.sendRequest(null, new RequestCallback() {
	     										public void onResponseReceived(Request req, final Response res) {
	     											MessageBox.hide();  
	     											if(res.getText().indexOf("errores") !=-1){
	     												//MessageBox.alert("Error", res.getText()); 
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
	     												store.reload();
	     												button.setId("");
	     												button.setDisabled(true);
	     												MainPanel.resetTimer();
	     												MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());  
	     												
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
	     				
	     			}  
	     		});  
	     		pagingToolbar.addButton(deleteEncounterButton);
	     		pagingToolbar.addSeparator();
	             
	             
	             ToolbarButton encountersExportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
	             	public void onClick(Button button, EventObject e) {
	             		com.google.gwt.user.client.Window.open("/semrs/encounterServlet?encounterAction=exportEncounters&searchList=true", "_self", ""); 

	             	}  
	             });  
	             encountersExportButton.setIconCls("excel-icon");
	             pagingToolbar.addButton(encountersExportButton);
	             pagingToolbar.addSeparator();
	             pagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
	             encountersGrid.setBottomToolbar(pagingToolbar);
	             
	             encountersGrid.addListener(new PanelListenerAdapter() {  
                     public void onRender(Component component) {  
                    	 store.load(0, pagingToolbar.getPageSize()); 
                     }  
                 }); 
	             
	            formPanel.add(encountersGrid, new AnchorLayoutData("100%"));
	            
               panel.add(formPanel);
               
	        }
	      
	    
	        return panel;
	    }

	    static ColumnModel createColModel() {
	      
	        ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
	        		new ColumnConfig("C.I Paciente", "patientId"),
	                new ColumnConfig("Nombres Paciente", "patientName"), 
	                new ColumnConfig("M&eacute;dico tratante", "encounterProvider"), 
	                new ColumnConfig("Fecha de Consulta", "encounterDate"),
	                new ColumnConfig("Referido De", "refferral"),
	                new ColumnConfig("Motivo", "reason"),
	                new ColumnConfig("Fecha de Creaci&oacute;n", "creationDate"),
	                new ColumnConfig("Usuario Creaci&oacute;n", "creationUser"),
	                new ColumnConfig("Fecha f&iacute;n", "endDate"),
	                new ColumnConfig("Fecha Ult. Modificaci&oacute;n", "lastEditDate"),
	                new ColumnConfig("Usuario Ult. Modificaci&oacute;n", "lastEditUser")
	
	        		
	        });
	        for (int i = 0; i < colModel.getColumnConfigs().length; i++){
	            ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
	        }
	        return colModel;
	    }
	   
	    public com.gwtext.client.widgets.Window getNewEncounterWindow(){
	    	  final com.gwtext.client.widgets.Window newEncounterWindow= new com.gwtext.client.widgets.Window();  
			   newEncounterWindow.setTitle("Nueva Consulta");  
			   newEncounterWindow.setWidth(450);  
			   newEncounterWindow.setHeight(290);    
			   newEncounterWindow.setLayout(new FitLayout());  
			   newEncounterWindow.setPaddings(5);  
			   newEncounterWindow.setResizable(false);
			   newEncounterWindow.setButtonAlign(Position.CENTER);  
			   newEncounterWindow.setModal(true);
			   newEncounterWindow.setIconCls("encounter-icon");
			   newEncounterWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);
			   
			  final FormPanel encounterForm = new FormPanel();  
			  encounterForm.setFrame(true);  
			  encounterForm.setWidth(450);  
			  encounterForm.setHeight(600);
			  encounterForm.setAutoScroll(true);
			
			     final Hidden providerIdEncounter = new Hidden();
			     providerIdEncounter.setName("providerIdEncounter");
			     
			     FieldDef[] cbFieldDefs = new FieldDef[] { 
			    		 new StringFieldDef("username"),
			    		 new StringFieldDef("name")
			     };

			     RecordDef cbRecordDef = new RecordDef(cbFieldDefs);

			     JsonReader cbReader = new JsonReader("response.value.items", cbRecordDef);
			     
			     HttpProxy cbProxy = new HttpProxy("/semrs/patientServlet?patientAction=getProviders", Connection.GET);
			     final Store cbStore = new Store(cbProxy,cbReader,true);
			     cbStore.load();
			     
			     final TextField patientId = new TextField("C&eacute;dula Paciente", "patientId");
					patientId.setStyle("textTransform: uppercase;");
					patientId.addListener(new FieldListenerAdapter(){
						public void onBlur(Field field) {
							String value = field.getValueAsString();
							field.setValue(value.toUpperCase());
						}

					});
					
				 encounterForm.add(patientId);
			     
			     final ComboBox newEncounterProviderCB = new ComboBox();  
			     newEncounterProviderCB.setName("providerIdCB2");
			     newEncounterProviderCB.setMinChars(1);  
			     newEncounterProviderCB.setFieldLabel("M&eacute;dico Tratante");  
			     newEncounterProviderCB.setStore(cbStore);
			     newEncounterProviderCB.setDisplayField("name");  
			     newEncounterProviderCB.setValueField("username");
			     newEncounterProviderCB.setMode(ComboBox.REMOTE);  
			     newEncounterProviderCB.setTriggerAction(ComboBox.ALL);  
			     newEncounterProviderCB.setEmptyText(" ");  
			     newEncounterProviderCB.setLoadingText("Buscando...");  
			     newEncounterProviderCB.setTypeAhead(true);  
			     newEncounterProviderCB.setSelectOnFocus(true);  
			     newEncounterProviderCB.setWidth(200);  
			     newEncounterProviderCB.setPageSize(10);
			     newEncounterProviderCB.addListener(new ComboBoxListenerAdapter(){
			         public void onSelect(ComboBox comboBox, Record record, int index){
			        	 providerIdEncounter.setValue(record.getAsString("username"));
			         }
			       });
			
			   
			     encounterForm.add(providerIdEncounter);
			     encounterForm.add(newEncounterProviderCB);

		        
		        MultiFieldPanel dateTimePanel = new MultiFieldPanel(); 
		        final DateField dateField = new DateField("Fecha","encounterDate", 140);
		        dateField.setMinValue(new Date());
	            dateTimePanel.addToRow(dateField, 250);  
		        final TimeField timeField = new TimeField();
		        timeField.setName("encounterTime");
		        timeField.setIncrement(60);
		        timeField.setMinValue("8:00am");  
		        timeField.setMaxValue("6:00pm"); 
		        timeField.setHideLabel(true);  
		        timeField.setWidth(80);  
		        dateTimePanel.addToRow(timeField, new ColumnLayoutData(1));
		        encounterForm.add(dateTimePanel);
		        
			    final TextArea textAreaRefferral = new TextArea("Referido de", "refferral");  
			    textAreaRefferral.setHideLabel(false);  
			    textAreaRefferral.setWidth(190);
			    textAreaRefferral.setHeight(80);
			    textAreaRefferral.setAllowBlank(true);
		        encounterForm.add(textAreaRefferral);
		        
		        final TextArea textAreaReason = new TextArea("Motivo de Consulta", "reason");  
		        textAreaReason.setHideLabel(false);  
		        textAreaReason.setWidth(190);
		        textAreaReason.setHeight(80);
		        textAreaReason.setAllowBlank(true);
		        encounterForm.add(textAreaReason);
		        
		        
		        Panel encounterPanel = new Panel();  
		        encounterPanel.setLayout(new ColumnLayout());  
		        encounterPanel.setBorder(false); 
		        encounterPanel.setButtonAlign(Position.CENTER);
		        
		        
		        final Button saveEncounterButton = new Button("Guardar");
		        saveEncounterButton.addListener(new ButtonListenerAdapter(){
		        	public void onClick(final Button button, EventObject e){
		        		ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea programar una nueva consulta para este paciente?", "Si", "No",  
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
		        		
		        		 RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/encounterServlet?encounterAction=newEncounter&"+encounterForm.getForm().getValues());
		        		 try {
		        			
		                     rb.sendRequest(null, new RequestCallback() {
		                        public void onResponseReceived(Request req, final Response res) {
		                           MessageBox.hide();  
				                   MessageBox.getDialog().close();
		                           if(res.getText().indexOf("errores") !=-1){
		                        	  // MessageBox.alert("Error", res.getText()); 
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
		                       	MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText() ,  
		                                 new MessageBox.AlertCallback() { 
									public void execute() {
									  MainPanel.resetTimer();
									  store.reload();
		                              newEncounterWindow.hide();
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
		        saveEncounterButton.setIconCls("save-icon");
		        encounterPanel.addButton(saveEncounterButton);
		        
		        Button cancelEncounterButton = new Button("Cancelar");
		        cancelEncounterButton.addListener(new ButtonListenerAdapter(){
		        	public void onClick(Button button, EventObject e){
		        		newEncounterWindow.hide();
		        	}
		        	
		        });
		        cancelEncounterButton.setIconCls("cancel-icon");
		        encounterPanel.addButton(cancelEncounterButton);  
		        encounterForm.add(encounterPanel);
			
				newEncounterWindow.add(encounterForm); 
				
				return newEncounterWindow;
	    }


		public com.gwtext.client.widgets.TabPanel getTabPanel() {
			return tabPanel;
		}

		public void setTabPanel(com.gwtext.client.widgets.TabPanel tabPanel) {
			this.tabPanel = tabPanel;
		}
	

}

