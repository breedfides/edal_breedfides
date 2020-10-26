package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.LanguageBridge;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.LongBridge;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.StringSetBridge;

@Entity
@Indexed
public class MyUntypedDataWrapper {
//	private Set<String> givenName = new HashSet<String>();
//	private Set<String> sureName = new HashSet<String>();
//	private Set<String> country = new HashSet<String>();
//	private Set<String> zip = new HashSet<String>();
//	private Set<String> addressLine = new HashSet<String>();
//	private Set<String> legalName = new HashSet<String>();
	private String givenName;
	private String sureName;
	private String country;
	private String zip;
	private String addressLine;
	private String legalName;
	private String identifier;
	private String mimeType;
	private String checkSum;
	private String algorithm;
	private Long size;
	private Locale language;
	private int versionId;
	private int id;
	private String strings;
	private MetaDataImplementation metaData;
	
	 public void setMetaData(MetaDataImplementation metaData) {
		this.metaData = metaData;
	}
	@OneToOne(fetch = FetchType.LAZY)
	public MetaDataImplementation getMetaData() {
		return metaData;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	@Column
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES
    )
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES
    )
	public String getSureName() {
		return sureName;
	}
	public void setSureName(String sureName) {
		this.sureName = sureName;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES
    )
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES
    )
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES
    )
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES
    )
	public String getLegalName() {
		return legalName;
	}
	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES
    )
	public String getStrings() {
		return strings;
	}
	public void setStrings(String strings) {
		this.strings = strings;
	}
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public String getCheckSum() {
		return checkSum;
	}
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES, valueBridge = @ValueBridgeRef(type = LongBridge.class)
    )
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES, valueBridge = @ValueBridgeRef(type = LanguageBridge.class)
    )
	public Locale getLanguage() {
		return language;
	}
	public void setLanguage(Locale language) {
		this.language = language;
	}
}
