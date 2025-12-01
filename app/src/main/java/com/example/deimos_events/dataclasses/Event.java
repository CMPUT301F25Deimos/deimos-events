package com.example.deimos_events.dataclasses;
/**
 * Represents an event within the Deimos Events application.
 * <p>
 * This class stores all metadata associated with an event, such as its title,
 * description, registration limits, QR code references, scheduling details,
 * and ownership information. It also provides getter and setter methods
 * for accessing and updating event properties.
 * </p>
 */
public class Event {
    /** Unique identifier for the event. */
    String id;
    /** Title of the event. */
    String title;
    /** The ID of the poster or organizer who created the event. */
    String posterId;
    /** Description of the event. */
    String description;
    /** Deadline by which participants must register. */
    String registrationDeadline;
    /** Maximum number of participants allowed. */
    Integer participantCap;
    /** Whether the event requires recording participant location. */
    Boolean recordLocation;
    /** The QR code ID associated with this event. */
    String qrCodeId;
    /** Guidelines provided for the event. */
    String guidelines;
    /** Criteria or rules applied to the event. */
    String criteria;
    /** Time at which the event occurs. */
    String time;
    /** Location of the event. */
    String location;
    /** Date of the event. */
    String date;
    /** The ID of the owner or organizer of this event. */
    String ownerId;
    /**
     * Constructs a new Event with the given parameters.
     *
     * @param id the unique event identifier
     * @param title the event title
     * @param posterId the ID of the poster or organizer
     * @param description a description of the event
     * @param registrationDeadline the deadline for registration
     * @param participantCap the maximum number of participants allowed
     * @param recordLocation whether the event requires recording location
     * @param qrCodeId the QR code associated with the event
     * @param ownerId the ID of the event owner
     */
    public Event(String id, String title, String posterId, String description, String registrationDeadline, Integer participantCap, Boolean recordLocation, String qrCodeId, String ownerId) {
        this.id = id;
        this.title = title;
        this.posterId = posterId;
        this.description = description;
        this.registrationDeadline = registrationDeadline;
        this.participantCap = participantCap;
        this.recordLocation = recordLocation;
        this.qrCodeId = qrCodeId;
        this.criteria = criteria;
        this.guidelines = guidelines;
        this.ownerId = ownerId;
    }
    /**
     * Default constructor for frameworks requiring an empty object.
     */
    public Event(){}
    /** @return the title of the event */
    public String getTitle() {
        return title;
    }
    /** @param title the event title to set */
    public void setTitle(String title) {
        this.title = title;
    }
    /** @return the poster or organizer ID */
    public String getPosterId() {
        return posterId;
    }
    /** @param posterId the ID of the poster to assign */
    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }
    /** @return the event description */
    public String getDescription() {
        return description;
    }
    /** @param description the description to set */
    public void setDescription(String description) {
        this.description = description;
    }
    /** @return the registration deadline */
    public String getRegistrationDeadline() {
        return registrationDeadline;
    }
    /** @param registrationDeadline the deadline to set */
    public void setRegistrationDeadline(String registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }
    /** @return the participant capacity */
    public Number getParticipantCap() {
        return participantCap;
    }
    /** @param participantCap the maximum number of participants */
    public void setParticipantCap(Integer participantCap) {
        this.participantCap = participantCap;
    }
    /** @return true if location recording is required */
    public Boolean getRecordLocation() {
        return recordLocation;
    }
    /** @param recordLocation whether location recording is required */
    public void setRecordLocation(Boolean recordLocation) {
        this.recordLocation = recordLocation;
    }
    /** @return the QR code ID */
    public String getQrCodeId() {
        return qrCodeId;
    }
    /** @param qrCodeId the QR code ID to set */
    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }
    /** @return the event ID */
    public String getId() {
        return id;
    }
    /** @param id the event ID to set */
    public void setId(String id) {
        this.id = id;
    }
    /** @param criteria the event criteria to set */
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
    /** @param guidelines the event guidelines to set */
    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }
    /** @return event guidelines */
    public String getGuidelines(){return guidelines;}
    /** @return event criteria */
    public String getCriteria(){return criteria;}
    /** @param date the event date to set */
    public void setDate(String date) {
        this.date = date;
    }
    /** @param location the event location to set */
    public void setLocation(String location) {
        this.location = location;
    }
    /** @param time the event time to set */
    public void setTime(String time) {
        this.time = time;
    }
    /** @return the event date */
    public String getDate() {
        return date;
    }
    /** @return the event location */
    public String getLocation() {
        return location;
    }
    /** @return the event time */
    public String getTime() {
        return time;
    }
    /** @return the ID of the event owner */
    public String getOwnerId() { return ownerId; }
}
