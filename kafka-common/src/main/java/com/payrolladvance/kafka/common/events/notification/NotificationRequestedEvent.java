package com.payrolladvance.kafka.common.events.notification;

import com.payrolladvance.kafka.common.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationRequestedEvent extends BaseEvent {
    private Long userId;
    private String title;
    private String message;
    private String type;  // ADVANCE_REQUEST, APPROVAL, DISBURSEMENT, REPAYMENT, SYSTEM
    private Long relatedEntityId;  // ID of the related entity (advance request, disbursement, etc.)
    private String relatedEntityType;  // Type of the related entity
    
    public NotificationRequestedEvent(Long userId, String title, String message, String type,
                                     Long relatedEntityId, String relatedEntityType) {
        super("NOTIFICATION_REQUESTED");
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
    }
}