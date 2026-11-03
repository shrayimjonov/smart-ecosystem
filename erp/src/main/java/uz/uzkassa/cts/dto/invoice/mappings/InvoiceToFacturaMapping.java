package uz.uzkassa.cts.dto.invoice.mappings;

import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.factura.*;
import uz.uzkassa.cts.enums.SupplierCategory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class InvoiceToFacturaMapping {

    private static final String LEASING_CATALOG_CODE = "08471003001000000"; // "08470001";
    private static final String LEASING_1C_CODE_P100 = "00000000505";
    private static final String LEASING_1C_CODE_N5 = "00000000218";
    private static final Integer ORIGIN = 2;
    private static final int RETAIL = 4;
    private static final int SERVICE_PROVIDER = 6;
    private static final String LEASING_CATALOG_NAME = "Онлайн контрольно-кассовая машина";
    private static final String LEASING_PACKAGE_CODE = "1501902";
    private static final String LEASING_PACKAGE_NAME = "шт.";
    private static final BigDecimal VAT_RATE = BigDecimal.valueOf(12);

    public static FacturaDTO mapInvoiceToFactura(ServiceOrderInvoice invoice) {
        FacturaDTO dto = new FacturaDTO();
        dto.setInvoiceId(invoice.getId());
        FacturaDocDTO facturaDocDTO = new FacturaDocDTO(invoice.getNumber(), invoice.getInvoiceDate().toLocalDate());
        dto.setFacturaDoc(facturaDocDTO);
//        dto.setFacturaId(invoice.getXFileId());

        Contract contract = invoice.getContract();
        Customer customer = contract.getCustomer();
        String xfileTin = customer.getXfileTin();
        if (StringUtils.isNotEmpty(xfileTin)) {
            dto.setBuyerTin(xfileTin);
            dto.setBuyerBranchTin(customer.getTin());
        } else
            dto.setBuyerTin(customer.getTin());

        uzbektelecomBranchChange(dto, customer);

        dto.setSellerTin(contract.getCompanyTin());

        dto.setContractDoc(new FacturaContractDTO(contract.getName(), contract.getCreatedDate().toLocalDate()));

        FacturaProductListDTO productListDTO = new FacturaProductListDTO();
        productListDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productListDTO.setHasVat(true);
        productListDTO.setTin(contract.getCompanyTin());

        List<FacturaProductDTO> productDTOS = new ArrayList<>();
        int productNo = 1;
        for (ServiceOrderInvoiceDetail detail : invoice.getServiceOrderInvoiceDetails()) {

            Tariff tariff = detail.getTariff();
            dto.setSellerCategory(tariff.getCategory().value);

            FacturaProductDTO productDTO = new FacturaProductDTO();
            productDTO.setOrdNo(productNo++ + "");
            productDTO.setName(tariff.getName());
            productDTO.setCode(tariff.getCode());
            //ID of measure in x-file shtuka is 1l
            productDTO.setMeasureId(85L);
//            productDTO.setBaseSumma(detail.getPrice1pc());
            productDTO.setProfitRate(BigDecimal.ZERO);
            productDTO.setCount(BigDecimal.valueOf(detail.getProductCount()));
            productDTO.setSumma(detail.getPrice1pc());
            productDTO.setExciseRate(BigDecimal.ZERO);
            productDTO.setExciseSum(BigDecimal.ZERO);
            productDTO.setDeliverySum(detail.getOverallPrice());
            productDTO.setVatRate(detail.getVatRate());
            productDTO.setVatSum(detail.getOverallVatPrice());
            productDTO.setDeliverySumWithVat(detail.getOverallSellPrice());
            productDTO.setWithoutVat(false);
            productDTO.setOrigin(tariff.getOrigin().getId());

            //ADDED BY NEW REQUIREMENTS OF NIC
            productDTO.setCatalogCode(tariff.getCatalogCode());
//            productDTO.setCatalogName(tariff.getCatalogName());
            productDTO.setPackageCode(tariff.getPackageCode());
            productDTO.setPackageName(tariff.getPackageName());

            productDTOS.add(productDTO);
        }
        productListDTO.setProducts(productDTOS);
        dto.setProductList(productListDTO);

        return dto;
    }

    public static FacturaDTO mapInvoiceToFactura(OneTimeOrderInvoice invoice) {
        FacturaDTO dto = new FacturaDTO();
        dto.setInvoiceId(invoice.getId());
        FacturaDocDTO facturaDocDTO = new FacturaDocDTO(invoice.getNumber(), invoice.getInvoiceDate().toLocalDate());
        dto.setFacturaDoc(facturaDocDTO);
//        dto.setFacturaId(invoice.getXFileId());
        Contract contract = invoice.getContract();

        Customer customer = contract.getCustomer();
        String xfileTin = customer.getXfileTin();
        if (StringUtils.isNotEmpty(xfileTin)) {
            dto.setBuyerTin(xfileTin);
            dto.setBuyerBranchTin(customer.getTin());
        } else
            dto.setBuyerTin(customer.getTin());

        uzbektelecomBranchChange(dto, customer);

        dto.setSellerTin(contract.getCompanyTin());

        dto.setContractDoc(new FacturaContractDTO(contract.getName(), contract.getCreatedDate().toLocalDate()));

        FacturaProductListDTO productListDTO = new FacturaProductListDTO();
        productListDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productListDTO.setHasVat(true);
        productListDTO.setTin(contract.getCompanyTin());

        List<FacturaProductDTO> productDTOS = new ArrayList<>();

        FacturaProductDTO productDTO = new FacturaProductDTO();
        productDTO.setOrdNo("1");
        CtsService service = invoice.getService();
        productDTO.setName(service.getName());
        productDTO.setCode(service.getCode());
        dto.setSellerCategory(service.getCategory().value);
        //ID of measure in x-file shtuka is 1l -- 85 is Service
        productDTO.setMeasureId(85L);
//        productDTO.setBaseSumma(invoice.getTotalPrice());
        productDTO.setProfitRate(BigDecimal.ZERO);
        productDTO.setCount(BigDecimal.valueOf(invoice.getTotalProductCount()));
        productDTO.setSumma(invoice.getTotalPrice().divide(BigDecimal.valueOf(invoice.getTotalProductCount()), MathContext.DECIMAL32));
        productDTO.setExciseRate(BigDecimal.ZERO);
        productDTO.setExciseSum(BigDecimal.ZERO);
        productDTO.setDeliverySum(invoice.getTotalPrice());
        productDTO.setVatRate(VAT_RATE);
        productDTO.setVatSum(invoice.getTotalVatPrice());
        productDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productDTO.setWithoutVat(false);
        productDTO.setOrigin(service.getOrigin().getId());
        //ADDED BY NEW REQUIREMENTS OF NIC
        productDTO.setCatalogCode(service.getCatalogCode());
//        productDTO.setCatalogName(service.getCatalogName());
        productDTO.setPackageCode(service.getPackageCode());
        productDTO.setPackageName(service.getPackageName());

        productDTOS.add(productDTO);
        productListDTO.setProducts(productDTOS);
        dto.setProductList(productListDTO);
        return dto;
    }

    public static FacturaDTO mapInvoiceToFactura(ActOfTransmissionInvoice invoice) {
        FacturaDTO dto = new FacturaDTO();
        dto.setInvoiceId(invoice.getId());
        FacturaDocDTO facturaDocDTO = new FacturaDocDTO(invoice.getNumber(), invoice.getInvoiceDate().toLocalDate());
        dto.setFacturaDoc(facturaDocDTO);
//        dto.setFacturaId(invoice.getXfileId());
        ContractForLeasing contract = invoice.getContract();

        Customer customer = contract.getCustomer();
        String xfileTin = customer.getXfileTin();
        if (StringUtils.isNotEmpty(xfileTin)) {
            dto.setBuyerTin(xfileTin);
            dto.setBuyerBranchTin(customer.getTin());
        } else
            dto.setBuyerTin(customer.getTin());

        uzbektelecomBranchChange(dto, customer);

        Company company = contract.getCompany();
        dto.setSellerTin(company.getTin());
        dto.setSellerCategory(SupplierCategory.RETAIL_TRADE.value);

        dto.setContractDoc(new FacturaContractDTO(contract.getNumber(), contract.getCreatedDate().toLocalDate()));

        FacturaProductListDTO productListDTO = new FacturaProductListDTO();
        productListDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productListDTO.setHasVat(true);
        productListDTO.setTin(company.getTin());

        List<FacturaProductDTO> productDTOS = new ArrayList<>();

        FacturaProductDTO productDTO = new FacturaProductDTO();
        productDTO.setOrdNo("1");
        productDTO.setName(invoice.getServiceName());

        ActOfTransmissionInvoiceDetail detail = invoice.getDetails().stream().findFirst().orElseThrow();

        String deviceCode = detail.getDevice().getModel().getCode();
        if (deviceCode.trim().equalsIgnoreCase("N5"))
            productDTO.setCode(LEASING_1C_CODE_N5);
        else
            productDTO.setCode(LEASING_1C_CODE_P100);

        productDTO.setMeasureId(1L);
//        productDTO.setBaseSumma(invoice.getTotalPrice());
        productDTO.setProfitRate(BigDecimal.ZERO);
        productDTO.setCount(BigDecimal.ONE);
        productDTO.setSumma(invoice.getTotalPrice());
        productDTO.setExciseRate(BigDecimal.ZERO);
        productDTO.setExciseSum(BigDecimal.ZERO);
        productDTO.setDeliverySum(invoice.getTotalPrice());
        productDTO.setVatRate(VAT_RATE);
        productDTO.setVatSum(invoice.getTotalVatPrice());
        productDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productDTO.setWithoutVat(false);
        productDTO.setOrigin(ORIGIN);
        //ADDED BY NEW REQUIREMENTS OF NIC
        productDTO.setCatalogCode(LEASING_CATALOG_CODE);
//        productDTO.setCatalogName(LEASING_CATALOG_NAME);
        productDTO.setPackageCode(LEASING_PACKAGE_CODE);
        productDTO.setPackageName(LEASING_PACKAGE_NAME);

        productDTOS.add(productDTO);
        productListDTO.setProducts(productDTOS);
        dto.setProductList(productListDTO);
        return dto;
    }

    public static FacturaDTO mapInvoiceToFactura(TradeInInvoice invoice) {
        FacturaDTO dto = new FacturaDTO();
        dto.setInvoiceId(invoice.getId());
        FacturaDocDTO facturaDocDTO = new FacturaDocDTO(invoice.getNumber(), invoice.getInvoiceDate().toLocalDate());
        dto.setFacturaDoc(facturaDocDTO);
//        dto.setFacturaId(invoice.getXfileId());
        TradeInContract contract = invoice.getContract();

        Customer customer = contract.getCustomer();
        String xfileTin = customer.getXfileTin();
        if (StringUtils.isNotEmpty(xfileTin)) {
            dto.setBuyerTin(xfileTin);
            dto.setBuyerBranchTin(customer.getTin());
        } else
            dto.setBuyerTin(customer.getTin());

        uzbektelecomBranchChange(dto, customer);

        Company company = contract.getCompany();
        dto.setSellerTin(company.getTin());
        dto.setSellerCategory(SupplierCategory.RETAIL_TRADE.value);

        dto.setContractDoc(new FacturaContractDTO(contract.getNumber(), contract.getCreatedDate().toLocalDate()));

        FacturaProductListDTO productListDTO = new FacturaProductListDTO();
        productListDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productListDTO.setHasVat(true);
        productListDTO.setTin(company.getTin());

        List<FacturaProductDTO> productDTOS = new ArrayList<>();
        TradeInInvoiceDetail detail = invoice.getDetail();

        FacturaProductDTO productDTO = new FacturaProductDTO();
        productDTO.setOrdNo("1");
        productDTO.setName(detail.getServiceName());
        productDTO.setCode(detail.getCode());
        productDTO.setMeasureId(1L);

        productDTO.setProfitRate(BigDecimal.ZERO);
        productDTO.setCount(BigDecimal.ONE);
        productDTO.setSumma(detail.getPriceFor1pc());
        productDTO.setExciseRate(BigDecimal.ZERO);
        productDTO.setExciseSum(BigDecimal.ZERO);
        productDTO.setDeliverySum(detail.getPriceFor1pc().multiply(BigDecimal.valueOf(detail.getTotalProductCount())));
        productDTO.setVatRate(VAT_RATE);
        productDTO.setVatSum(detail.getTotalVatPrice());
        productDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productDTO.setWithoutVat(false);
        productDTO.setOrigin(detail.getOrigin().getId());
        //ADDED BY NEW REQUIREMENTS OF NIC
        productDTO.setCatalogCode(detail.getCatalogCode());
//        productDTO.setCatalogName(detail.getCatalogName());
        productDTO.setPackageCode(detail.getPackageCode());
        productDTO.setPackageName(detail.getPackageName());

        productDTOS.add(productDTO);
        productListDTO.setProducts(productDTOS);
        dto.setProductList(productListDTO);
        return dto;
    }

    public static FacturaDTO mapSaleInvoiceToFactura(SaleInvoice invoice) {
        FacturaDTO dto = new FacturaDTO();
        dto.setInvoiceId(invoice.getId());
        FacturaDocDTO facturaDocDTO = new FacturaDocDTO(invoice.getNumber(), invoice.getInvoiceDate().toLocalDate());
        dto.setFacturaDoc(facturaDocDTO);
        SaleContract saleContract = invoice.getSaleContract();

        Customer customer = saleContract.getCustomer();
        String xfileTin = customer.getXfileTin();
        if (StringUtils.isNotEmpty(xfileTin)) {
            dto.setBuyerTin(xfileTin);
            dto.setBuyerBranchTin(customer.getTin());
        } else {
            dto.setBuyerTin(customer.getTin());
        }
        uzbektelecomBranchChange(dto, customer);

        Company company = saleContract.getCompany();
        dto.setSellerTin(company.getTin());
        dto.setSellerCategory(SupplierCategory.RETAIL_TRADE.value);

        dto.setContractDoc(new FacturaContractDTO(saleContract.getNumber(), saleContract.getCreatedDate().toLocalDate()));

        FacturaProductListDTO productListDTO = new FacturaProductListDTO();
        productListDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productListDTO.setHasVat(true);
        productListDTO.setTin(company.getTin());

        List<FacturaProductDTO> productDTOS = new ArrayList<>();
        int productNo = 1;

        for (SaleInvoiceDetail saleInvoiceDetail : invoice.getDetails()) {
            FacturaProductDTO productDTO = new FacturaProductDTO();

            productDTO.setOrdNo(productNo++ + "");
            productDTO.setName(saleInvoiceDetail.getServiceName());
            productDTO.setCode(saleInvoiceDetail.getCode());
            productDTO.setMeasureId(1L);
            productDTO.setProfitRate(BigDecimal.ZERO);
            productDTO.setCount(BigDecimal.valueOf(saleInvoiceDetail.getTotalProductCount()));
            productDTO.setSumma(saleInvoiceDetail.getPriceFor1pc());
            productDTO.setExciseRate(BigDecimal.ZERO);
            productDTO.setExciseSum(BigDecimal.ZERO);
            productDTO.setDeliverySum(saleInvoiceDetail.getPriceFor1pc().multiply(BigDecimal.valueOf(saleInvoiceDetail.getTotalProductCount())));
            productDTO.setVatRate(VAT_RATE);
            productDTO.setVatSum(saleInvoiceDetail.getTotalVatPrice());
            productDTO.setDeliverySumWithVat(saleInvoiceDetail.getTotalSellPrice());
            productDTO.setWithoutVat(false);

            productDTO.setOrigin(saleInvoiceDetail.getOrigin().getId());
            productDTO.setCatalogCode(saleInvoiceDetail.getCatalogCode());
            productDTO.setPackageCode(saleInvoiceDetail.getPackageCode());
            productDTO.setPackageName(saleInvoiceDetail.getPackageName());

            productDTOS.add(productDTO);
        }
        productListDTO.setProducts(productDTOS);
        dto.setProductList(productListDTO);
        return dto;
    }

    public static FacturaDTO mapInvoiceToFactura(RentInvoice invoice) {
        FacturaDTO dto = new FacturaDTO();
        dto.setInvoiceId(invoice.getId());
        FacturaDocDTO facturaDocDTO = new FacturaDocDTO(invoice.getNumber(), invoice.getInvoiceDate().toLocalDate());
        dto.setFacturaDoc(facturaDocDTO);
//        dto.setFacturaId(invoice.getXfileId());
        RentOffer offer = invoice.getRentOffer();

        Customer customer = offer.getCustomer();
        String xfileTin = customer.getXfileTin();
        if (StringUtils.isNotEmpty(xfileTin)) {
            dto.setBuyerTin(xfileTin);
            dto.setBuyerBranchTin(customer.getTin());
        } else
            dto.setBuyerTin(customer.getTin());

        uzbektelecomBranchChange(dto, customer);

        Company company = offer.getCompany();
        dto.setSellerTin(company.getTin());
        dto.setSellerCategory(SupplierCategory.SERVICE_PROVIDER.value);

        dto.setContractDoc(new FacturaContractDTO(offer.getNumber(), offer.getCreatedDate().toLocalDate()));

        FacturaProductListDTO productListDTO = new FacturaProductListDTO();
        productListDTO.setDeliverySumWithVat(invoice.getTotalSellPrice());
        productListDTO.setHasVat(true);
        productListDTO.setTin(company.getTin());

        List<FacturaProductDTO> productDTOS = new ArrayList<>();
        int productNo = 1;
        for (RentInvoiceDetail detail : invoice.getDetails()) {

            FacturaProductDTO productDTO = new FacturaProductDTO();
            productDTO.setOrdNo(productNo++ + "");
            productDTO.setName(detail.getServiceName() + " " + detail.getDeviceSerialNumber());
            productDTO.setCode(detail.getCode());
            productDTO.setMeasureId(1L);

            productDTO.setProfitRate(BigDecimal.ZERO);
            productDTO.setCount(BigDecimal.ONE);
            productDTO.setSumma(detail.getPriceFor1pc());
            productDTO.setExciseRate(BigDecimal.ZERO);
            productDTO.setExciseSum(BigDecimal.ZERO);
            productDTO.setDeliverySum(detail.getPriceFor1pc().multiply(BigDecimal.valueOf(detail.getTotalProductCount())));
            productDTO.setVatRate(VAT_RATE);
            productDTO.setVatSum(detail.getTotalVatPrice());
            productDTO.setDeliverySumWithVat(detail.getTotalSellPrice().multiply(BigDecimal.valueOf(detail.getTotalProductCount())));
            productDTO.setWithoutVat(false);
            productDTO.setOrigin(detail.getOrigin());
            //ADDED BY NEW REQUIREMENTS OF NIC
            productDTO.setCatalogCode(detail.getCatalogCode());
//            productDTO.setCatalogName(detail.getCatalogName());
            productDTO.setPackageCode(detail.getPackageCode());
            productDTO.setPackageName(detail.getPackageName());

            productDTOS.add(productDTO);
        }
        productListDTO.setProducts(productDTOS);
        dto.setProductList(productListDTO);
        return dto;
    }

    private static void uzbektelecomBranchChange(FacturaDTO dto, Customer customer) {
        if (customer.getTin().equals("203366731")) {
            dto.setBuyerBranchTin("00004");
        }
    }
}
