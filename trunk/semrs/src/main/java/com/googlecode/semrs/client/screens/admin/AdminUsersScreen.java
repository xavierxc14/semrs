package com.googlecode.semrs.client.screens.admin;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
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
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.VType;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowParams;



public class AdminUsersScreen extends ShowcasePanel {
	
	
	
     private com.gwtext.client.widgets.TabPanel tabPanel;
     
     public static boolean reloadFlag = false;
     
     FieldDef[] fieldDefs = new FieldDef[] { 
     		new StringFieldDef("username"),
             new StringFieldDef("name"), 
             new StringFieldDef("lastName"), 
             new StringFieldDef("email"), 
     		new StringFieldDef("phoneNumber"),
     		new StringFieldDef("mobile"),
     		new StringFieldDef("lastEditDate"),	
     		new StringFieldDef("lastEditUser"),
     		new StringFieldDef("lastLogin"),	
     		new StringFieldDef("enabled")
     };

     RecordDef recordDef = new RecordDef(fieldDefs);

     JsonReader reader = new JsonReader("response.value.items", recordDef);

     HttpProxy proxy = new HttpProxy("/semrs/userServlet", Connection.GET);
     final Store store = new Store(proxy,reader,true);
     final PagingToolbar pagingToolbar = new PagingToolbar(store);
     
     public AdminUsersScreen(){
    	//reader.setVersionProperty("response.value.version");
         reader.setTotalProperty("response.value.total_count");
         reader.setId("username");
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
	            
	            
	           
	            store.setDefaultSort("username", SortDir.ASC);
	            store.addStoreListener(new StoreListenerAdapter() {
	            	public void onLoadException(Throwable error) {
	            		  //Check for session expiration
	            		  RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
	            	        try {
	            	            rb.sendRequest(null, new RequestCallback() {

	            	                public void onError(Request request, Throwable exception) {
	            	                	MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de usuarios.");
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
					            			MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de usuarios.");
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
	            formPanel.setTitle("B&uacute;squeda de Usuarios");  
	            formPanel.setWidth(900);  
	            formPanel.setLabelWidth(100); 
	            formPanel.setPaddings(5, 5, 5, 0);  
	            formPanel.setLabelAlign(Position.TOP);
	            formPanel.setIconCls("user-icon");
 
	            Panel topPanel = new Panel();  
	            topPanel.setLayout(new ColumnLayout());  

	            //create first panel and add fields to it  
	            Panel columnOnePanel = new Panel();  
	            columnOnePanel.setLayout(new FormLayout());  

	            TextField username = new TextField("Usuario", "username");
	            columnOnePanel.add(username, new AnchorLayoutData("65%"));  

	            TextField email = new TextField("Email", "email");  
	            email.setVtype(VType.EMAIL); 
	            columnOnePanel.add(email, new AnchorLayoutData("65%"));  

	            //add first panel as first column with 50% of the width  
	            topPanel.add(columnOnePanel, new ColumnLayoutData(.5));  

	            //create second panel and add fields to it  
	            Panel columnTwoPanel = new Panel();  
	            columnTwoPanel.setLayout(new FormLayout());  

	            TextField name = new TextField("Nombre", "name");   
	            columnTwoPanel.add(name, new AnchorLayoutData("65%"));  

	            TextField lastName = new TextField("Apellido", "lastName");  
	            columnTwoPanel.add(lastName, new AnchorLayoutData("65%"));  

	            //add the second panel as the second column to the top panel to take up the other 50% width  
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
	           // formPanel.add(proxyPanel);
	            
	            formPanel.setMonitorValid(true);
	            formPanel.addListener(new FormPanelListenerAdapter() {
	                public void onClientValidation(FormPanel formPanel, boolean valid) {
	                	search.setDisabled(!valid);
	                }
	            });
  
               
	           GridView view = new GridView(){
	        	   public String getRowClass(Record record, int index, RowParams rowParams, Store store) {
	        		   if(record.getAsString("enabled").startsWith("N")){
	        			   return "redClass";
	        		   }

	        		   return "";
	               }
	           };
	           view.setEmptyText("No hay Registros");
	           view.setAutoFill(true);
	           view.setForceFit(true);
	 
              /* 
	            BufferedGridToolbar toolbar = new BufferedGridToolbar(view);
	            toolbar.setDisplayInfo(true);
               
	            BufferedRowSelectionModel brsm = new BufferedRowSelectionModel();
               */
	            
	            GridPanel grid = new GridPanel(store, createColModel());
	            grid.setEnableDragDrop(false);
	            grid.setWidth(850);
	            grid.setHeight(368);
	            grid.setTitle("Lista de Usuarios");
	            grid.setLoadMask(true);  
	            grid.setSelectionModel(new RowSelectionModel());  
	            grid.setFrame(true);  
	            grid.setView(view);
	            grid.addGridCellListener(new GridCellListener() {  

					
					public void onCellDblClick(GridPanel grid, int rowIndex,
							int colIndex, EventObject e) {
					    Record r = grid.getStore().getAt(rowIndex);
					   // AddEditUserScreen addEditPanel = new AddEditUserScreen();
					   // addEditPanel.setUserId(r.getAsString("username"));
					   // addEditPanel.setId("addEdit");
					   ShowcasePanel.addEditUserScreen.setUserId(r.getAsString("username"));
					    showScreen(getTabPanel(),ShowcasePanel.addEditUserScreen,"Agregar/Editar Usuario","user-add-icon","addEditUser");
		                 // grid.getStore().reload();
						
					}

					
					public void onCellClick(GridPanel grid, int rowIndex,
							int colIndex, EventObject e) {
				      
						
					}

		
					public void onCellContextMenu(GridPanel grid, int rowIndex,
							int cellIndex, EventObject e) {
						// TODO Auto-generated method stub
						
					}
	                });  
	            
	           
	            pagingToolbar.setPageSize(10);
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
	            ToolbarButton exportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
	            	public void onClick(Button button, EventObject e) {
	            		Window.open("/semrs/userServlet?export=true", "_self", ""); 
	
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
	        		new ColumnConfig("Nombre de Usuario", "username"),
	                new ColumnConfig("Nombre", "name"), 
	                new ColumnConfig("Apellido", "lastName"), 
	                new ColumnConfig("Email", "email"),
	                new ColumnConfig("Telef&oacute;no", "phoneNumber"),
	                new ColumnConfig("M&oacute;vil", "mobile"),
	                new ColumnConfig("F.Modificaci&oacute;n", "lastEditDate"),
	                new ColumnConfig("U.Modificaci&oacute;n", "lastEditUser"),
	                new ColumnConfig("&Uacute;ltimo Login", "lastLogin"),
	                new ColumnConfig("Activo?", "enabled")
	        		
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
	

}
