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
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
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

public class AdminSymptomsScreen extends ShowcasePanel {

	private com.gwtext.client.widgets.TabPanel tabPanel;

	private FormPanel symptomForm = null;

	private com.gwtext.client.widgets.Window editSymptomWindow = null;

	public static boolean reloadFlag = false;

	private static String symptomId;

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

	HttpProxy proxy = new HttpProxy("/semrs/symptomServlet", Connection.GET);
	final Store store = new Store(proxy,reader,true);
	final PagingToolbar pagingToolbar = new PagingToolbar(store);

	public AdminSymptomsScreen(){
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
								MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de s&iacute;ntomas.");
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
									MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de s&iacute;ntomas.");
								}
							}
						});
					 } catch (RequestException e) {
         	        	MessageBox.alert("Error", "Ha ocurrido un error al tratar de conectarse con el servidor.");
         	        }
				}
				public void onDataChanged(Store store) {
					MainPanel.resetTimer();
				}

			});




			final FormPanel formPanel = new FormPanel();
			formPanel.setFrame(true);  
			formPanel.setTitle("B&uacute;squeda de S&iacute;ntomas");  
			formPanel.setWidth(900);  
			formPanel.setLabelWidth(100); 
			formPanel.setPaddings(5, 5, 5, 0);  
			formPanel.setLabelAlign(Position.TOP);  
			formPanel.setIconCls("symptoms-icon");

			Panel topPanel = new Panel();  
			topPanel.setLayout(new ColumnLayout());  

//			create first panel and add fields to it  
			Panel columnOnePanel = new Panel();  
			columnOnePanel.setLayout(new FormLayout());  

			TextField id = new TextField("C&oacute;digo", "id");
			columnOnePanel.add(id, new AnchorLayoutData("65%"));  

//			add first panel as first column with 50% of the width  
			topPanel.add(columnOnePanel, new ColumnLayoutData(.5));  

//			create second panel and add fields to it  

			Panel columnTwoPanel = new Panel();  
			columnTwoPanel.setLayout(new FormLayout());  

			TextField symptomName = new TextField("Nombre", "name");  
			columnTwoPanel.add(symptomName, new AnchorLayoutData("65%")); 
			topPanel.add(columnTwoPanel, new ColumnLayoutData(0.5));  



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
//			formPanel.add(proxyPanel);

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

			GridPanel grid = new GridPanel(store, createColModel(true, true));
			grid.setEnableDragDrop(false);
			grid.setWidth(850);
			grid.setHeight(420);
			grid.setTitle("Lista de S&iacute;ntomas");
			grid.setLoadMask(true);  
			grid.setSelectionModel(new RowSelectionModel());  
			grid.setFrame(true);  
			grid.setView(view);
			grid.addGridCellListener(new GridCellListener() {  


				public void onCellDblClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
					Record r = grid.getStore().getAt(rowIndex);
					String recordId = r.getAsString("id");
					setSymptomId(recordId);
					//symptomId = recordId;
					com.gwtext.client.widgets.Window symptomWindow = getEditSymptomWindow(false);
					symptomWindow.show();
					//getEditGroupWindow(symptomId).show();
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
			ToolbarButton newLabTestWindow = new ToolbarButton("Nuevo S&iacute;ntoma", new ButtonListenerAdapter() {  
				public void onClick(Button button, EventObject e) {
					setSymptomId("");
					//symptomId = "";
					com.gwtext.client.widgets.Window drugWindow = getEditSymptomWindow(true);
					drugWindow.show();
					// getEditGroupWindow("").show();
				}  
			});  
			newLabTestWindow.setIconCls("add-icon");
			pagingToolbar.addButton(newLabTestWindow);
			pagingToolbar.addSeparator();

//			pagingToolbar.addButton(deleteGroupButton);
//			pagingToolbar.addSeparator();
			ToolbarButton exportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
				public void onClick(Button button, EventObject e) {
					Window.open("/semrs/symptomServlet?export=true", "_self", ""); 

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


	public com.gwtext.client.widgets.TabPanel getTabPanel() {
		return tabPanel;
	}

	public void setTabPanel(com.gwtext.client.widgets.TabPanel tabPanel) {
		this.tabPanel = tabPanel;
	}


	public com.gwtext.client.widgets.Window getEditSymptomWindow(boolean isNew){

		if(editSymptomWindow!=null){
			editSymptomWindow.removeAll(true);
		}

		editSymptomWindow = new com.gwtext.client.widgets.Window();  
		editSymptomWindow.setTitle("Editar S&iacute;ntoma");  
		editSymptomWindow.setWidth(700);  
		editSymptomWindow.setHeight(600);    
		editSymptomWindow.setLayout(new FitLayout());  
		editSymptomWindow.setPaddings(5);  
		editSymptomWindow.setResizable(true);
		editSymptomWindow.setButtonAlign(Position.CENTER);  
		editSymptomWindow.setModal(true);
		editSymptomWindow.setId("editSymptomWindow");
		editSymptomWindow.setIconCls("symptoms-icon");
		editSymptomWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);
		editSymptomWindow.setMaximizable(true);
		//editSymptomWindow.setMinimizable(true);

		RecordDef recordDef = new RecordDef(new FieldDef[]{   
				new StringFieldDef("id"),  
				new StringFieldDef("name"),  
				new StringFieldDef("description"),
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

		if(symptomForm!=null){
			symptomForm.clear();
		}

		symptomForm = new FormPanel(); 

		symptomForm.setReader(reader);  
		symptomForm.setErrorReader(errorReader); 
		symptomForm.setFrame(true);  
		symptomForm.setWidth(700);  
		symptomForm.setHeight(600);
		symptomForm.setAutoScroll(true);
		symptomForm.setId("editSymptomForm");

		Panel proxyPanel = new Panel();  
		proxyPanel.setBorder(true);  
		proxyPanel.setBodyBorder(false);
		proxyPanel.setCollapsible(false);  
		proxyPanel.setLayout(new FormLayout());
		proxyPanel.setButtonAlign(Position.CENTER);
		//proxyPanel.setIconCls("groupProxyPanel");

		FieldSet symptomFS = new FieldSet("Informaci&oacute;n de S&iacute;ntoma");  
		symptomFS.setCollapsible(true);
		symptomFS.setFrame(false);  
		symptomFS.setId("symptomFS");

		TextField symptomIdText = new TextField("C&oacute;digo","id",190);
		symptomIdText.setId("symptomIdText");
		if(!getSymptomId().equals("")){
			symptomIdText.setReadOnly(true);
		}
		symptomIdText.setAllowBlank(false);
		symptomIdText.setStyle("textTransform: uppercase;");
		symptomIdText.addListener(new FieldListenerAdapter(){
			public void onBlur(Field field) {
				String value = field.getValueAsString();
				field.setValue(value.toUpperCase());
			}

		});
		symptomFS.add(symptomIdText);
		
	    TextField loadSuccess = new TextField("loadSuccess","loadSuccess",190);
	    loadSuccess.setId("symptomLoadSuccess");
	    loadSuccess.setVisible(false);
	    symptomFS.add(loadSuccess);

		TextField symptomNameText = new TextField("Nombre", "name", 190);  
		symptomNameText.setId("symptomNameText");
		symptomNameText.setAllowBlank(false);
		symptomFS.add(symptomNameText); 


		HtmlEditor symptomDesc = new HtmlEditor("Descripci&oacute;n", "description");  
		symptomDesc.setId("symptomDesc");
		// drugDesc.setWidth(190);
		symptomDesc.setHeight(200);  
		symptomFS.add(symptomDesc); 

		symptomForm.add(symptomFS);

	
		 
        FieldDef[] gridFieldDefs = new FieldDef[] { 
	     		new StringFieldDef("id"),
	            new StringFieldDef("name")
	     };

	    RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
	    JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
	    HttpProxy gridProxy = new HttpProxy("/semrs/symptomServlet?symptomEdit=getRelatedSymptoms&symptomId="+getSymptomId()+"&relatedSymptoms=true", Connection.GET);
	    final Store gridStore = new Store(gridProxy,gridReader,true);
	    gridStore.load();
	    
		FieldDef[] gridFieldDefs2 = new FieldDef[] { 
  		     		new StringFieldDef("id"),
  		            new StringFieldDef("name")
  		  };

  		RecordDef gridRecordDef2 = new RecordDef(gridFieldDefs2);
  		JsonReader gridReader2 = new JsonReader("response.value.items", gridRecordDef2);
  		HttpProxy gridProxy2 = new HttpProxy("/semrs/symptomServlet?symptomEdit=getRelatedSymptoms&symptomId="+getSymptomId(), Connection.GET);
  		final Store innerGridStore = new Store(gridProxy2,gridReader2,true);
		
        FieldDef[] gridFieldDefsD = new FieldDef[] { 
	     		new StringFieldDef("id"),
	            new StringFieldDef("name")
	     };

	    RecordDef gridRecordDefD = new RecordDef(gridFieldDefsD);
	    JsonReader gridReaderD = new JsonReader("response.value.items", gridRecordDefD);
	    HttpProxy gridProxyD = new HttpProxy("/semrs/symptomServlet?symptomEdit=getDrugs&symptomId="+getSymptomId()+"&relatedDrugs=true", Connection.GET);
	    final Store gridStoreD = new Store(gridProxyD,gridReaderD,true);
	    gridStoreD.load();
	    
		   FieldDef[] gridFieldDefsD2 = new FieldDef[] { 
  		     		new StringFieldDef("id"),
  		            new StringFieldDef("name")
  		     };

  	    RecordDef gridRecordDefD2 = new RecordDef(gridFieldDefsD2);
  		JsonReader gridReaderD2 = new JsonReader("response.value.items", gridRecordDefD2);
  		HttpProxy gridProxyD2 = new HttpProxy("/semrs/symptomServlet?symptomEdit=getDrugs&symptomId="+getSymptomId(), Connection.GET);
  		final Store innerGridStoreD = new Store(gridProxyD2,gridReaderD2,true);
  		 
 		FieldSet relatedSymptomsFS = new FieldSet("S&iacute;ntomas");  
		relatedSymptomsFS.setId("relatedSymptomsFS");
		relatedSymptomsFS.setCollapsible(true);
		relatedSymptomsFS.setFrame(false);  	 
  		
 		FieldSet symptomDrugsFS = new FieldSet("Medicamentos");  
		symptomDrugsFS.setId("symptomDrugsFS");
		symptomDrugsFS.setCollapsible(true);
		symptomDrugsFS.setFrame(false);  
  		 
  		relatedSymptomsFS.add(getGrid(gridStore,innerGridStore ,createColModel(false,false), "S&iacute;ntomas Relacionados", "symptoms-icon"), new AnchorLayoutData("100%")); 
		
  		symptomDrugsFS.add(getGrid(gridStoreD,innerGridStoreD ,createColModel(false,false), "Medicamentos Relacionados", "drugs-icon"), new AnchorLayoutData("100%"));

		symptomForm.add(symptomDrugsFS);
		
		symptomForm.add(relatedSymptomsFS);


		final Button saveButton = new Button("Guardar");
		saveButton.setId("symptomSaveButton");
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
					RequestBuilder saveRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/symptomServlet?symptomEdit=submit&isNew="+getSymptomId()+"&"+symptomForm.getForm().getValues()+"&relatedSymptoms="+
							getRecordValues(gridStore.getRecords())+"&symptomDrugs="+getRecordValues(gridStoreD.getRecords()));
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
								editSymptomWindow.hide();
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
		delete.setId("symptomDeleteButton");
		if(getSymptomId().equals("")){
			delete.setDisabled(true);
		}else{
			delete.setDisabled(false);
		}
		final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/symptomServlet?symptomEdit=delete&id="+getSymptomId());
		delete.addListener(new ButtonListenerAdapter(){
			public void onClick(final Button button, EventObject e){

				ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar este S&iacute;ntoma?", "Si", "No",  
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
													editSymptomWindow.hide();
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
		cancel.setId("cancelSymptomButton");
		cancel.addListener(new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e){
				editSymptomWindow.hide();
			}

		});
		cancel.setIconCls("cancel-icon");
		proxyPanel.addButton(cancel);  

		symptomForm.add(proxyPanel);

		symptomForm.setMonitorValid(true);
		symptomForm.addListener(new FormPanelListenerAdapter() {
			public void onClientValidation(FormPanel formPanel, boolean valid) {
				saveButton.setDisabled(!valid);
			}
		});
		symptomForm.doLayout();
		
		 if(!isNew){
			 symptomForm.getForm().load("/semrs/symptomServlet?symptomEdit=load&id="+getSymptomId(), null, Connection.GET, "Cargando...");
			 symptomForm.getForm().addListener(new FormListenerAdapter(){
		    	   public void onActionComplete(Form form, int httpStatus, String responseText) {
		    		  if(form.findField("symptomLoadSuccess").getValueAsString().equals("false")){
		    				MessageBox.show(new MessageBoxConfig() {  
		    					{  
		    						setTitle("Error");
		    						setMsg("Este s&iacute;ntoma no existe");
		    						setIconCls(MessageBox.ERROR);
		    					    setModal(true);
		    					    setButtons(MessageBox.OK);
		    					    setCallback(new MessageBox.PromptCallback() { 
		    						public void execute(
		    								String btnID,
		    								String text) {
		    							store.reload();
		    							editSymptomWindow.close();
		    							
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
							setMsg("Ocurrio un error al tratar de obtener este s&iacute;ntoma");
							setIconCls(MessageBox.ERROR);
						    setModal(true);
						    setButtons(MessageBox.OK);
						    setCallback(new MessageBox.PromptCallback() { 
							public void execute(
									String btnID,
									String text) {
								store.reload();
								editSymptomWindow.close();
								
							}  
	                        });  
						}  
					});
		    	    }
		    	   
		       });
		      }
		editSymptomWindow.add(symptomForm);  
		editSymptomWindow.doLayout();
		return editSymptomWindow;
	}

	public String getSymptomId() {
		return symptomId;
	}

	public void setSymptomId(String symptomId) {
		this.symptomId = symptomId;
	}


}
