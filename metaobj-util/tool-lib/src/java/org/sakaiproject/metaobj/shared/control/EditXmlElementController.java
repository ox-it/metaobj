/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /root/osp/src/portfolio/org/theospi/portfolio/shared/control/EditXmlElementController.java,v 1.17 2005/08/30 21:27:09 jellis Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.control;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;

import java.util.Map;
/**
 * Created by IntelliJ IDEA.
 * <p/>
 * User: John Ellis
 * <p/>
 * Date: Apr 20, 2004
 * <p/>
 * Time: 3:31:25 PM
 * <p/>
 * To change this template use File | Settings | File Templates.
 */
public class EditXmlElementController extends XmlControllerBase
   implements CustomCommandController, LoadObjectController, CancelableController {
   protected final Log logger = LogFactory.getLog(getClass());
   public Object formBackingObject(Map request, Map session, Map application) {
      StructuredArtifactHomeInterface home =
         (StructuredArtifactHomeInterface) getHomeFactory().getHome(getSchemaName(session));
      return home.createInstance();
   }
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (request.get(EditedArtifactStorage.STORED_ARTIFACT_FLAG) == null) {
         StructuredArtifactHomeInterface home =
            (StructuredArtifactHomeInterface) getHomeFactory().getHome(getSchemaName(session));
         StructuredArtifact bean = (StructuredArtifact)incomingModel;
         bean = (StructuredArtifact)home.load(bean.getId());
         EditedArtifactStorage sessionBean = new EditedArtifactStorage(bean.getCurrentSchema(),
            bean);
         session.put(EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY,
            sessionBean);
         return bean;
      }
      else {
         EditedArtifactStorage sessionBean = (EditedArtifactStorage)session.get(
            EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY);
         return sessionBean.getCurrentElement();
      }           
   }
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      ElementBean bean = (ElementBean) requestModel;
      if (request.get("submitButton") == null) {
         return handleNonSubmit(bean, request, session, application, errors);
      }
      getValidator().validate(bean, errors, true);
      if (errors.hasErrors()) {
         return null;
      }
      WritableObjectHome home = (WritableObjectHome) getHomeFactory().getHome(getSchemaName(session));
      try {
         home.store((StructuredArtifact)bean);
      } catch (PersistenceException e) {
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
            e.getDefaultMessage());
      }
      return new ModelAndView("success", "schema",
         getSchemaName(session));
   }
}