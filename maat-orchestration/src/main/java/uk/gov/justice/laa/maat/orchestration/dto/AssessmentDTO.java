package uk.gov.justice.laa.maat.orchestration.dto;

public class AssessmentDTO
{
	private IOJAppealDTO						iojAppeal;
	private FinancialAssessmentDTO				financialAssessmentDTO;
	
	public AssessmentDTO(){
		reset();
	}
	
	public void reset()	{
		this.iojAppeal = new IOJAppealDTO();
		this.financialAssessmentDTO = new FinancialAssessmentDTO();
	}
	
	public IOJAppealDTO getIojAppeal()
	{
		return iojAppeal;
	}

	public void setIojAppeal(IOJAppealDTO iojAppeal)
	{
		this.iojAppeal = iojAppeal;
	}

	public FinancialAssessmentDTO getFinancialAssessmentDTO() {
		return financialAssessmentDTO;
	}

	public void setFinancialAssessmentDTO(
			FinancialAssessmentDTO financialAssessmentDTO) {
		this.financialAssessmentDTO = financialAssessmentDTO;
	}

}
