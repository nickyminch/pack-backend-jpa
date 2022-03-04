## Copyright 2015 JAXIO http://www.jaxio.com
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##
$output.generateIf($project.isAuditLogPresent())##
$output.java($Service, "AuditLogService")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.require("java.util.List")##
$output.require("java.util.concurrent.BlockingQueue")##
$output.require("java.util.concurrent.LinkedBlockingQueue")##
$output.require("javax.inject.Inject")##
$output.require("org.slf4j.Logger")##
$output.require("org.slf4j.LoggerFactory")##
$output.require("org.hibernate.Session")##
$output.require("org.hibernate.SessionFactory")##
$output.require("org.hibernate.Transaction")##
$output.require("org.springframework.scheduling.annotation.Scheduled")##
$output.require($Context, "UserContext")##
$output.require($project.auditLog.model)##

$output.dynamicAnnotationTakeOver("javax.inject.Named","javax.inject.Singleton","org.springframework.context.annotation.Lazy(false)")##
public class $output.currentClass {
    private static final Logger log = LoggerFactory.getLogger(${output.currentClass}.class);
    private static final int DEFAULT_BATCH_INSERT_SIZE = 50;
    @Inject
    private SessionFactory sessionFactory;
    protected BlockingQueue<${project.auditLog.model.type}> queue = new LinkedBlockingQueue<${project.auditLog.model.type}>(1000);
    protected int batchInsertSize = DEFAULT_BATCH_INSERT_SIZE;

    @Scheduled(fixedDelay = 1 * 1000)
    public void batchInsert() {
        List<${project.auditLog.model.type}> httpEvents = newArrayList();
        int size = queue.drainTo(httpEvents, batchInsertSize);
        if (size != 0) {
            batchInsert(httpEvents);
        }
    }

    public void event(AuditEvent auditEvent) {
        event(auditEvent, null);
    }

    public void event(AuditEvent auditEvent, String string1) {
        event(auditEvent, string1, null);
    }

    public void event(AuditEvent auditEvent, String string1, String string2) {
        event(auditEvent, string1, string2, null);
    }

    public void event(AuditEvent auditEvent, String string1, String string2, String string3) {
        ${project.auditLog.model.type} ${project.auditLog.model.var} = new ${project.auditLog.model.type}();
        ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.author.setter}(UserContext.getUsername());
        ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.event.setter}(auditEvent.name());
        ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.stringAttribute1.setter}(string1);
        ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.stringAttribute2.setter}(string2);
        ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.stringAttribute3.setter}(string3);
        log(${project.auditLog.model.var});
    }

    public void log(${project.auditLog.model.type} ${project.auditLog.model.var}) {
        setupDefaults(${project.auditLog.model.var});
        queue.add(${project.auditLog.model.var});
    }

    private void setupDefaults(${project.auditLog.model.type} ${project.auditLog.model.var}) {
        if (${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.author.getter}() == null) {
            ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.author.setter}(UserContext.getUsername());
        }
        if (${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.eventDate.getter}() == null) {
$output.require("$project.auditLog.auditLogAttributes.eventDate.fullType")##
            ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.eventDate.setter}(new ${project.auditLog.auditLogAttributes.eventDate.type}());
        }
    }

    private void batchInsert(List<${project.auditLog.model.type}> ${project.auditLog.model.vars}) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            for (${project.auditLog.model.type} ${project.auditLog.model.var} : ${project.auditLog.model.vars}) {
                session.save(${project.auditLog.model.var});
            }
            session.flush();
            transaction.commit();
            log.debug("Added {} ${project.auditLog.model.type} in database", ${project.auditLog.model.vars}.size());
        } catch (Exception e) {
            log.error("Error while inserting ${project.auditLog.model.type}", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}