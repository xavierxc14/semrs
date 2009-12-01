package com.googlecode.semrs.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowParams;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.portal.Portal;
import com.gwtext.client.widgets.portal.PortalColumn;
import com.gwtext.client.widgets.portal.Portlet;
import com.gwtextux.client.widgets.ManagedIFramePanel;

public class MainTab extends Panel {


	private static TabPanel tabPanel;

	public MainTab() {
		setBorder(false);
		setLayout(new FitLayout());
		setAutoScroll(true);
	}

	protected void afterRender() {
		
		
		FieldDef[] patientFieldDefs = new FieldDef[] { 
		  		new StringFieldDef("id"),
	            new StringFieldDef("name"), 
	            new StringFieldDef("lastName"), 
	            new StringFieldDef("sex"), 
	    		new StringFieldDef("birthDate"),
	    		new StringFieldDef("age"),
	    		new StringFieldDef("phoneNumber"),	
	    		new StringFieldDef("mobile"),
	    		new StringFieldDef("creationDate"),	
	    		new StringFieldDef("lastEditDate"),
	    		new StringFieldDef("lastEditUser"),	
	    		new StringFieldDef("provider"),
	    		new StringFieldDef("voided"),	
	    		new StringFieldDef("lastEncounterDate"),
	    		new StringFieldDef("edit")
	    };

	    RecordDef patientRecordDef = new RecordDef(patientFieldDefs);

	    JsonReader patientReader = new JsonReader("response.value.items", patientRecordDef);

	    HttpProxy patientProxy = new HttpProxy("/semrs/patientServlet?patientAction=listPatients&limit=10", Connection.GET);
	    final Store patientStore = new Store(patientProxy,patientReader,false);
	    patientStore.setDefaultSort("creationDate", SortDir.DESC);
	    patientStore.setRemoteSort(false);
	    patientReader.setTotalProperty("response.value.total_count");
	    patientReader.setId("id");
	    
	    
	    
	    
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
    			new StringFieldDef("edit")
    	};

    	RecordDef recordDef = new RecordDef(fieldDefs);

    	JsonReader reader = new JsonReader("response.value.items", recordDef);
		reader.setTotalProperty("response.value.total_count");
		reader.setId("id");
		

    	
      HttpProxy gridProxy = new HttpProxy("/semrs/userServlet?userEdit=listEncountersForUser", Connection.GET);
      final Store store = new Store(gridProxy,reader,true);
      store.setDefaultSort("encounterDate", SortDir.ASC);
	    
	    
		

		Panel panel = new Panel();
		panel.setId("showcase-view");
		panel.setBorder(false);
		//panel.setPaddings(15);  
		panel.setCollapsible(true);
		panel.setLayout(new FitLayout());
		panel.setTitle("Inicio");

		//create a portal  
		Portal portal = new Portal();

		//create portal columns  
		PortalColumn firstCol = new PortalColumn();
		firstCol.setPaddings(10, 10, 0, 10);
		
		final Portlet introPortlet = new Portlet("Bienvenido", getPortletMessage());
		introPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {
				introPortlet.hide();
			}
		}));
	
		
		final ManagedIFramePanel  activeChartFrame = new ManagedIFramePanel ("/semrs/activeChart.html");  
	    Panel activeChartPanel = new Panel();  
	    activeChartPanel.setLayout(new FitLayout());  
	    activeChartPanel.add(activeChartFrame);  
	    activeChartPanel.setHeight(300);
	    
	    
	    final Portlet activeChartPortlet = new Portlet();  
	    activeChartPortlet.setTitle("Pacientes Activos/Inactivos");  
	    activeChartPortlet.setLayout(new FitLayout());  
	    activeChartPortlet.add(activeChartPanel);

	    activeChartPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			public void execute() {
				MainPanel.resetTimer();
				activeChartFrame.refresh();  
			
			}
		}));
		
	    activeChartPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {
				activeChartPortlet.hide();
			}
		}));
		
		
	    
		firstCol.add(introPortlet);
		firstCol.add(activeChartPortlet);
		portal.add(firstCol, new ColumnLayoutData(.28));
		
		final Portlet gridPortlet = new Portlet();  
		gridPortlet.setTitle("Consultas para Hoy");  
		gridPortlet.setLayout(new FitLayout());  
		GridPanel grid = getCurrentUserEncountersGrid(store);
		grid.setFrame(false);
		gridPortlet.add(grid);

		gridPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			public void execute() {
				MainPanel.resetTimer();
				store.reload();
			}
		}));
		
		gridPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {
				gridPortlet.hide();
			}
		}));
		
		
		
		 
		final ManagedIFramePanel  ageChartFrame = new ManagedIFramePanel ("/semrs/ageChart.html");  
	    Panel ageChartPanel = new Panel();  
	    ageChartPanel.setLayout(new FitLayout());  
	    ageChartPanel.add(ageChartFrame);  
	    ageChartPanel.setWidth(550);
	    ageChartPanel.setHeight(300);
	    
	    
	    final Portlet ageChartPortlet = new Portlet();  
	    ageChartPortlet.setTitle("Distribuci&oacute;n Por Edad");  
	    ageChartPortlet.setLayout(new FitLayout());  
	    ageChartPortlet.add(ageChartPanel);

	    ageChartPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			public void execute() {
				MainPanel.resetTimer();
				ageChartFrame.refresh();  
			
			}
		}));
		
	    ageChartPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {
				ageChartPortlet.hide();
			}
		}));
		

		PortalColumn secondCol = new PortalColumn();
		secondCol.setPaddings(10, 10, 0, 10);
		
		secondCol.add(gridPortlet);
		secondCol.add(ageChartPortlet);
		portal.add(secondCol, new ColumnLayoutData(.44));
		
		
		final Portlet patientGridPortlet = new Portlet();  
		patientGridPortlet.setTitle("&Uacute;ltimos 10 Pacientes Registrados");  
		patientGridPortlet.setLayout(new FitLayout());  
		GridPanel patientGrid = getPatientGrid(patientStore);
		patientGrid.setFrame(false);
		patientGridPortlet.add(patientGrid);

		patientGridPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			public void execute() {
				MainPanel.resetTimer();
				patientStore.reload();
			}
		}));
		
		patientGridPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {
				patientGridPortlet.hide();
			}
		}));
		
		 
		final ManagedIFramePanel  chartFrame = new ManagedIFramePanel ("/semrs/sexChart.html");  
	    Panel chartPanel = new Panel();  
	    chartPanel.setLayout(new FitLayout());  
	    chartPanel.add(chartFrame);  
	    chartPanel.setHeight(300);
	    
	    
	    final Portlet chartPortlet = new Portlet();  
	    chartPortlet.setTitle("Distribuci&oacute;n Demogr&aacute;fica de Pacientes");  
	    chartPortlet.setLayout(new FitLayout());  
		chartPortlet.add(chartPanel);

		chartPortlet.addTool(new Tool(Tool.REFRESH, new Function(){
			public void execute() {
				MainPanel.resetTimer();
				chartFrame.refresh();  
			
			}
		}));
		
		chartPortlet.addTool(new Tool(Tool.CLOSE, new Function(){
			public void execute() {
				chartPortlet.hide();
			}
		}));

		//third column  
		PortalColumn thirdCol = new PortalColumn();
		thirdCol.setPaddings(10, 10, 0, 10);
		thirdCol.add(patientGridPortlet);
		thirdCol.add(chartPortlet);
	
		portal.add(thirdCol, new ColumnLayoutData(.28));

		panel.add(portal);

		Viewport viewport = new Viewport(panel);

		add(panel);
	}



	private GridPanel getPatientGrid(final Store store) {
		

        
        
        GridView view = new GridView(){
     	   public String getRowClass(Record record, int index, RowParams rowParams, Store store) {
     		   if(record.getAsString("voided").startsWith("N")){
     			   return "redClass";
     		   }

     		   return "";
            }
        };
        view.setEmptyText("No hay Registros");
        view.setAutoFill(true);
        view.setForceFit(true);
        
        
        GridPanel grid = new GridPanel(store, createPatientColModel());
        grid.setEnableDragDrop(false);
        grid.setWidth(500);
        grid.setHeight(280);
        grid.setLoadMask(true);  
        grid.setSelectionModel(new RowSelectionModel());  
        grid.setFrame(true);  
        grid.setView(view);
        grid.setAutoExpandColumn("id");
        grid.addGridCellListener(new GridCellListener() {  

			
			public void onCellDblClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
			    Record r = grid.getStore().getAt(rowIndex);
			    ShowcasePanel.adminPatientScreen.setPatientId(r.getAsString("id"));
			    String voided = r.getAsString("voided");
			    String edit = r.getAsString("edit");
			    if(voided.startsWith("S")){
			    	voided = "false";
			    }else{
			    	voided = "true";
			    }
			    
			    String lastEncounterDate =	r.getAsString("lastEncounterDate");
			    if(lastEncounterDate == null || lastEncounterDate.trim().equals("")){
			    	lastEncounterDate = "";
			    }
			    if(edit.startsWith("t")){
			    ShowcasePanel.adminPatientScreen.flag1 = false;
			    ShowcasePanel.adminPatientScreen.setLastEncounterDate(lastEncounterDate.trim());
			    ShowcasePanel.adminPatientScreen.setVoided(voided);
			    ShowcasePanel.adminPatientScreen.setTabPanel(tabPanel);
			    ShowcasePanel.adminPatientScreen.setMainPanelStore(grid.getStore());
			    showScreen(getTabPanel(),ShowcasePanel.adminPatientScreen, "Detalle de Paciente","patient-detail-icon","admPatientDetail");
			    }
				
			}

			
			public void onCellClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
		      
				
			}


			public void onCellContextMenu(GridPanel grid, int rowIndex,
					int cellIndex, EventObject e) {
				// TODO Auto-generated method stub
				
			}
            });  
        
     
        
        
        grid.addListener(new PanelListenerAdapter() {  
                     public void onRender(Component component) {  
                    	 store.load(); 
                     }  
                 }); 
	
		return grid;
	}
	
	
    private GridPanel getCurrentUserEncountersGrid(final Store store) {
    	
    
        final PagingToolbar pagingToolbar = new PagingToolbar(store);
       
    	
      
      
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
        
        final GridPanel encountersGrid = new GridPanel(store, createColModel());
        encountersGrid.setEnableDragDrop(false);
        encountersGrid.setWidth(550);
        encountersGrid.setHeight(280);
        encountersGrid.setLoadMask(true);  
        encountersGrid.setSelectionModel(new RowSelectionModel());  
        encountersGrid.setFrame(true);  
        encountersGrid.setView(encountersView);
        encountersGrid.addGridCellListener(new GridCellListener() {  

			
			public void onCellDblClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
			    Record r = grid.getStore().getAt(rowIndex);
			    String edit = r.getAsString("edit");
			    if(edit.startsWith("t")){
			        ShowcasePanel.adminEncounterScreen.setEncounterId(r.getAsString("id"));
				    ShowcasePanel.adminEncounterScreen.flag1 = false;
			    	ShowcasePanel.adminEncounterScreen.setEdit(true);
			    	ShowcasePanel.adminEncounterScreen.setCurrentEncountersStore(store);
			    	showScreen(getTabPanel(),ShowcasePanel.adminEncounterScreen, "Editar Consulta","encounter-icon","admEncounterDetail");
			    }
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
        ToolbarButton exportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
        	public void onClick(Button button, EventObject e) {
        		Window.open("/semrs/userServlet?userEdit=exportEncounters", "_self", ""); 

        	}  
        });  
        exportButton.setIconCls("excel-icon");
        pagingToolbar.addButton(exportButton);
        pagingToolbar.addSeparator();
        pagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
        encountersGrid.setBottomToolbar(pagingToolbar);
        
        encountersGrid.addListener(new PanelListenerAdapter() {  
            public void onRender(Component component) {  
           	 store.load(0, pagingToolbar.getPageSize()); 
            }  
        }); 
		
		return encountersGrid;
	}
	
	
	private GridPanel getCurrentEncountersGrid() {
		
		return null;
	}
	
	
	private String getPortletMessage(){
		return  "<p align='center'><img src='images/logo/semrs_logo.png' align='center'/> " +
		        "<p align='center'>Bienvenido al Sistema de Registro y Control de Pacientes." +
                "<p align='center'>Utilize el menu a la izquierda para navegar por los modulos disponibles." +  
                "<p align='center'>Para mas informaci&oacute;n consulte con el administrador.";
	}
	
	
	  static ColumnModel createColModel() {
	      
	        ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
	        		new ColumnConfig("C.I Paciente", "patientId"),
	                new ColumnConfig("Nombres Paciente", "patientName"), 
	                new ColumnConfig("M&eacute;dico tratante", "encounterProvider"), 
	                new ColumnConfig("Fecha de Consulta", "encounterDate"),
	                new ColumnConfig("Referido De", "refferral"),
	                new ColumnConfig("Motivo", "reason")
	               // new ColumnConfig("Fecha de Creaci&oacute;n", "creationDate"),
	               // new ColumnConfig("Usuario Creaci&oacute;n", "creationUser")
	
	        		
	        });
	        for (int i = 0; i < colModel.getColumnConfigs().length; i++){
	            ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
	        }
	        return colModel;
	    }
	  
	  
	  static ColumnModel createPatientColModel() {
	      
	        ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
	        		new ColumnConfig("C&eacute;dula de Identidad", "id"),
	                new ColumnConfig("Nombres", "name"), 
	                new ColumnConfig("Apellidos", "lastName"), 
	                new ColumnConfig("Sexo", "sex"),
	                new ColumnConfig("Fecha de Nacimiento", "birthDate"),
	                new ColumnConfig("Edad", "age"),
	                //new ColumnConfig("Fecha Ult.Consulta", "lastEncounterDate")
	
	        		
	        });
	        for (int i = 0; i < colModel.getColumnConfigs().length; i++){
	            ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
	        }
	        return colModel;
	    }  
	  
	  
	  

	public TabPanel getTabPanel() {
		return tabPanel;
	}

	public static void setTabPanel(TabPanel tabPanel) {
	       MainTab.tabPanel = tabPanel;
	}

	
	public void showScreen(TabPanel appTabPanel, Panel panel, String title, String iconCls, String screenName) {
        String panelID = panel.getId();
        if (appTabPanel.hasItem(panelID)) {
        	//panel.doLayout();
            appTabPanel.scrollToTab(panel, true);
            appTabPanel.activate(panelID);
            //appTabPanel.doLayout();
        } else {
            if (!panel.isRendered()) {
                panel.setTitle(title);
                if (iconCls == null) {
                    iconCls = "plugins-nav-icon";
                }
                panel.setIconCls(iconCls);
            }
            appTabPanel.add(panel);
           // appTabPanel.doLayout();
            appTabPanel.activate(panel.getId());
           // appTabPanel.doLayout();
            //panel.show();
        }
        //panel.show();
        appTabPanel.doLayout();
        History.newItem(screenName);
    }

	

}