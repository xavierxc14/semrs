package com.googlecode.semrs.client.screens.patient;

import java.util.Date;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.core.EventObject; 
import com.gwtext.client.core.Position;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.VType;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.layout.FormLayout;

public class AddPatientScreen extends ShowcasePanel {
	
	FormPanel formPanel = null;

	boolean flag1 = false;

	protected void onActivate() {
		if (!flag1) {
			getViewPanel();
		}
		flag1 = false;
	}

	public Panel getViewPanel() {

		flag1 = true;

		// if (panel == null) {
		if (formPanel != null) {
			formPanel.clear();
			//formPanel.getForm().reset();
		}


		panel = new Panel();
		panel.setAnimCollapse(true);
		MainPanel.resetTimer();

		formPanel = new FormPanel();
		formPanel.setFrame(true);
		formPanel.setId("addPatientForm");
		formPanel.setTitle("Registrar Nuevo Paciente");
		formPanel.setWidth(900);
		formPanel.setLabelWidth(100);
		formPanel.setPaddings(5, 5, 5, 0);
		// formPanel.setLabelAlign(Position.TOP);
		formPanel.setLabelWidth(160);  
		formPanel.setIconCls("patient-icon");
		formPanel.setAnimCollapse(true);
		
		
		Panel proxyPanel = new Panel();  
		proxyPanel.setId("patientProxyPanel");
		proxyPanel.setBorder(true);  
		proxyPanel.setBodyBorder(false);
		proxyPanel.setCollapsible(false);  
		proxyPanel.setLayout(new FormLayout());
		proxyPanel.setButtonAlign(Position.CENTER);


		final FieldSet patientFS = new FieldSet("Informaci&oacute;n del Paciente");  
		patientFS.setId("savePatientFS");
		patientFS.setCollapsible(false);
		patientFS.setHideCollapseTool(true);
		//patientFS.setCollapsed(true);
		patientFS.setFrame(false);  
		patientFS.setAnimCollapse(true);
		patientFS.setLabelWidth(130); 

		
		final TextField patientId = new TextField("N&uacute;mero de C&eacute;dula", "id", 90);
		patientId.setId("savePatientId");
		patientId.setAllowBlank(false);
		patientId.setMaxLength(10);
		patientId.setMinLength(2);
		patientId.setStyle("textTransform: uppercase;");
		//patientIdTooltip.applyTo(patientId);
		//patientId.markInvalid("Por favor introduzca un n&uacute;mero de c&eacute;dula v&aacute;lido");


		patientId.addListener(new FieldListenerAdapter() {
			public void onBlur(Field field) {
				String value = field.getValueAsString();
				field.setValue(value.toUpperCase());
					RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,
							"/semrs/patientServlet?patientAction=validateExisting&id="
									+ field.getValueAsString().toUpperCase());
					try {

						rb.sendRequest(null, new RequestCallback() {
							public void onResponseReceived(Request req,
									Response res) {
								if (res.getText().indexOf("Error") != -1 && res.getText().indexOf("Paciente") == -1) {
									patientId
									.markInvalid("Por favor introduzca un n&uacute;mero de c&eacute;dula v&aacute;lido");
								}else if (res.getText().indexOf("Error") != -1 && res.getText().indexOf("Paciente") != -1) {
									patientId
									.markInvalid("El n&uacute;mero de c&eacute;dula ya esta registrado en el sistema");
								} else if (res.getText().equals("")) {
									patientId
									.markInvalid("Por favor introduzca un n&uacute;mero de c&eacute;dula v&aacute;lido");
									MessageBox.alert("Error", "Error interno");
								} else if (res.getText().indexOf("Exito") != -1) {
									MainPanel.resetTimer();
									patientId.validate();
								}

							}

							public void onError(Request req, Throwable exception) {
								MessageBox.alert("Error", "Error interno");
							}

						});

					} catch (RequestException re) {
						MessageBox.alert("Error", "Error interno");
					}
				
			}
			
			   public void onChange(Field field, Object newVal, Object oldVal) {
				   
					RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,
							"/semrs/patientServlet?patientAction=validateExisting&id="
									+ field.getValueAsString().toUpperCase());
					try {

						rb.sendRequest(null, new RequestCallback() {
							public void onResponseReceived(Request req,
									Response res) {
								if (res.getText().indexOf("Error") != -1 && res.getText().indexOf("Paciente") == -1) {
									patientId
									.markInvalid("Por favor introduzca un n&uacute;mero de c&eacute;dula v&aacute;lido");
								}else if (res.getText().indexOf("Error") != -1 && res.getText().indexOf("Paciente") != -1) {
									patientId
									.markInvalid("El n&uacute;mero de c&eacute;dula ya esta registrado en el sistema");
								} else if (res.getText().equals("")) {
									patientId
									.markInvalid("Por favor introduzca un n&uacute;mero de c&eacute;dula v&aacute;lido");
									MessageBox.alert("Error", "Error interno");
								} else if (res.getText().indexOf("Exito") != -1) {
									MainPanel.resetTimer();
									patientId.validate();
								}

							}

							public void onError(Request req, Throwable exception) {
								MessageBox.alert("Error", "Error interno");
							}

						});

					} catch (RequestException re) {
						MessageBox.alert("Error", "Error interno");
					}
				   
			    }

		});

		
	
		
		final TextField patientName = new TextField("Nombres", "name", 260);
		patientName.setId("savePatientName");
		patientName.setAllowBlank(false);
		final TextField patientLastName = new TextField("Apellidos", "lastName", 260);
		patientLastName.setId("savePatientLastName");
		patientLastName.setAllowBlank(false);
		final DateField birthDate = new DateField("Fecha de Nacimiento", "birthDate", 190);  
		birthDate.setId("savePatientBirthDate");
		birthDate.setAllowBlank(false);  
		birthDate.setMaxValue(new Date());
		birthDate.setReadOnly(true);
		
		
		Store store = new SimpleStore(new String[]{"abbr", "sex"}, new String[][]{ new String[]{"M","Masculino"}, new String[]{"F", "Femenino"}});  
		store.load();  
		final ComboBox sexCB = new ComboBox();  
		sexCB.setFieldLabel("Sexo");  
		sexCB.setHiddenName("sex");  
		sexCB.setStore(store);  
		sexCB.setDisplayField("sex");  
		sexCB.setTypeAhead(true);  
		sexCB.setMode(ComboBox.LOCAL);  
		sexCB.setTriggerAction(ComboBox.ALL);  
		sexCB.setSelectOnFocus(true);  
		sexCB.setWidth(190); 
		sexCB.setName("sex");
		sexCB.setAllowBlank(false);
		sexCB.setId("savePatientSex");
		sexCB.setReadOnly(true);
		
		final TextField birthPlace = new TextField("Lugar de Nacimiento", "birthPlace", 190);  
		birthPlace.setId("savePatientBirthPlace");
				
		final TextField phone = new TextField("Telef&oacute;no", "phoneNumber", 190);
		phone.setId("savePatientPhone");
		phone.setAllowBlank(false);
		phone.setVtype(VType.ALPHANUM);
		
		final TextField mobile = new TextField("M&oacute;vil", "mobile", 190);
		mobile.setId("savePatientMobile");
		mobile.setVtype(VType.ALPHANUM);

		final TextArea addressTextArea = new TextArea("Direcci&oacute;n", "address");  
		addressTextArea.setHideLabel(false);  
		addressTextArea.setWidth(190);
		addressTextArea.setHeight(80);
		addressTextArea.setAllowBlank(false);
		addressTextArea.setId("savePatientAddressTextArea");
		
		final TextField email = new TextField("Email", "email", 190);  
		email.setVtype(VType.EMAIL);
		email.setId("savePatientEmail");
		
		patientFS.add(patientId);
		patientFS.add(patientName);
		patientFS.add(patientLastName);
		patientFS.add(sexCB);
		patientFS.add(birthDate);
		patientFS.add(birthPlace);
		patientFS.add(phone);
		patientFS.add(mobile);
		patientFS.add(addressTextArea);
		patientFS.add(email);
		
		//formPanel.add(patientId);
		//formPanel.add(validateIdButton);
		
		
		final Button saveButton = new Button("Guardar");
		saveButton.setId("savePatientButton");
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
		
					RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/patientServlet?patientAction=savePatient&isNew=true&"+formPanel.getForm().getValues());
					try {

						rb.sendRequest(null, new RequestCallback() {
							public void onResponseReceived(Request req, final Response res) {
								MessageBox.hide();  
		                        MessageBox.getDialog().close();
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
									if(res.getText().indexOf("identidad") !=-1){
										patientId.markInvalid("el campo C&eacute;dula de identidad debe tener un formato de tipo VXXXXXXXX o EXXXXXXXX");
									}
									if(res.getText().indexOf("Paciente") !=-1){
										patientId.markInvalid("El n&uacute;mero de c&eacute;dula ya esta registrado en el sistema");
									}
									if(res.getText().indexOf("Email") !=-1){
										email.markInvalid("El email suministrado ya existe en el sistema");
									}
									
									if(!patientId.getValueAsString().equals("")){
										patientId.markInvalid("Este campo es obligatorio");
									}
									if(!patientName.getValueAsString().equals("")){
										patientName.markInvalid("Este campo es obligatorio");
									}
									if(!patientLastName.getValueAsString().equals("")){
										patientLastName.markInvalid("Este campo es obligatorio");
									}
									if(!birthDate.getValueAsString().equals("")){
										birthDate.markInvalid("Este campo es obligatorio");
									}
									if(!sexCB.getValueAsString().equals("")){
										sexCB.markInvalid("Este campo es obligatorio");
									}
									if(!phone.getValueAsString().equals("")){
										phone.markInvalid("Este campo es obligatorio");
									}
									if(!addressTextArea.getValueAsString().equals("")){
										addressTextArea.markInvalid("Este campo es obligatorio");
									}
									
								 }else if(res.getText().equals("")){
		                        	   MessageBox.hide();
			                           MessageBox.alert("Error", "Error interno"); 
		                           }else{
									MainPanel.resetTimer();
									MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText() ,  
			                                 new MessageBox.AlertCallback() { 
										public void execute() {
											ListPatientsScreen.reloadFlag = true;
											formPanel.getForm().reset();
										
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

		});
		saveButton.setIconCls("save-icon");
		
		proxyPanel.addButton(saveButton);
		
		
		Button cancel = new Button("Cancelar");
		cancel.setId("cancelPatientButton");
        cancel.addListener(new ButtonListenerAdapter(){
        	public void onClick(Button button, EventObject e){
        		MainPanel.centerPanel.remove(MainPanel.centerPanel.getActiveTab());
        	}
        	
        });
        cancel.setIconCls("cancel-icon");
        proxyPanel.addButton(cancel);
        
        Button clear = new Button("Limpiar");
    	clear.setIconCls("clear-icon");
    	clear.addListener(new ButtonListenerAdapter(){

    		public void onClick(Button button, EventObject e){
    			formPanel.getForm().reset();

    		}

    	});
    	proxyPanel.addButton(clear);  
        		
		
        patientFS.add(proxyPanel);

	
        
        formPanel.add(patientFS);
		formPanel.doLayout();
		panel.add(formPanel);

		panel.doLayout();

		return panel;


		
	}

}
