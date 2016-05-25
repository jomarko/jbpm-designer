/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;


public interface DesignerEditorConstants extends
        Messages {
    DesignerEditorConstants INSTANCE = GWT.create(DesignerEditorConstants.class);

    String Add();

    String businessProcess();

    String businessProcessResourceTypeDescription();

    String Cancel();

    String Constant();

    String containsAlreadyMerged();

    String Custom();

    String Data_Input();

    String Data_Inputs();

    String Data_Input_and_Assignment();

    String Data_Inputs_and_Assignments();

    String Data_Output_and_Assignment();

    String Data_Output();

    String Data_Outputs();

    String Data_Outputs_and_Assignments();

    String Data_IO();

    String Edit();

    String Enter_constant();

    String Enter_type();

    String eventType();

    String standardHelp();

    String generalProcessInfo();

    String guidedBusinessProcess();

    String guidedBusinessProcessWizard();

    String Invalid_character_in_name();

    String jsonSwaggerFile();

    String mergeInvalidTaskCount();

    String noCompatibleVariableForParameter();

    String No_Data_Input();

    String No_Data_Output();

    String Only_single_entry_allowed();

    String processName();

    String processNameHelp();

    String processDescription();

    String processInputs();

    String processParticipants();

    String processStartEvent();

    String processTasks();

    String Save();

    String services();

    String signalEventType();

    String signalHelp();

    String Source();

    String standardEventType();

    String signalFormat();

    String splitInvalidRowCount();

    String swaggerUploadResults();

    String Target();

    String This_input_should_be_entered_as_a_property_for_the_task();

    String timerEventType();

    String timerDateFormat();

    String timerDateHelp();

    String timerCycleAndDelayFormat();

    String timerCycleHelp();

    String timerDelayHelp();

    String variablesHelp();

    String Removed_invalid_characters_from_name();

    String A_Data_Input_with_this_name_already_exists();

    String ProcessModel();

    String ConfirmCloseBusinessProcessEditor();
}
