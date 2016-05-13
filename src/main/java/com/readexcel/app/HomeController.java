package com.readexcel.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController implements HandlerExceptionResolver {

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	@Autowired
	private CommonsMultipartResolver multipartResolver;

	public CommonsMultipartResolver getMultipartResolver() {
		return multipartResolver;
	}

	public void setMultipartResolver(CommonsMultipartResolver multipartResolver) {
		this.multipartResolver = multipartResolver;
	}

	@RequestMapping("/welcome")
	public ModelAndView showJsp() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("attachement", new Attachment());
		map.put("serverTime", "time is");

		return new ModelAndView("home", map);
	}

	@RequestMapping("/openexcel")
	public String openExcel(
			@ModelAttribute("formImport") Attachment attachment,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		logger.info("RequestController->getexceldata");
		MultipartFile multipartFile = null;
		MultipartHttpServletRequest defRequest = null;

		try {

			if (multipartResolver.isMultipart(request)) {
				defRequest = (MultipartHttpServletRequest) request;
				multipartFile = attachment.getUploadedFile();
			}

		} catch (Exception e) {
			response.getWriter().print("error");

		} finally {
			multipartResolver.cleanupMultipart(defRequest);
		}

		String fileName = multipartFile.getOriginalFilename();
		int dot = fileName.lastIndexOf(".");
		String extension = fileName.substring(dot + 1).toLowerCase();
		StringBuilder builder = new StringBuilder();
		PrintWriter out = response.getWriter();

		try {

			if ("xls".equalsIgnoreCase(extension)) {
				HSSFWorkbook wb = new HSSFWorkbook(multipartFile
						.getInputStream());
				HSSFSheet sheet = wb.getSheetAt(0);
				Iterator rowIter = sheet.rowIterator();
				rowIter.next();

				while (rowIter.hasNext()) {
					HSSFRow row = (HSSFRow) rowIter.next();
					String firstColumn = (row.getCell(0) == null ? "" : row
							.getCell(0).getStringCellValue());
					String secondColumn = (row.getCell(1) == null ? "" : row
							.getCell(0).getStringCellValue());

					if (StringUtils.isEmpty(firstColumn)) {
						out.println("nodata");
						break;
					}
					logger.info("Excel values are " + firstColumn);
					builder.append(firstColumn);
					if (rowIter.hasNext()) {
						builder.append("@");
					}
				}
				out.println(builder.toString());

			} else if ("xlsx".equalsIgnoreCase(extension)) {
				InputStream inputStream = multipartFile.getInputStream();
				XSSFWorkbook wb = new XSSFWorkbook(inputStream);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator rowIter = sheet.rowIterator();
				rowIter.next();

				while (rowIter.hasNext()) {
					XSSFRow row = (XSSFRow) rowIter.next();
					String firstColumn = (row.getCell(0) == null ? "" : row
							.getCell(0).getStringCellValue());
					String secondColumn = (row.getCell(1) == null ? "" : row
							.getCell(0).getStringCellValue());
					if (StringUtils.isEmpty(firstColumn)) {
						out.println("nodata");
						break;
					}
					logger.info("Excel values are " + firstColumn);
					builder.append(firstColumn).append("|");
					builder.append(secondColumn).append("|");

					if (rowIter.hasNext()) {
						builder.append("@");
					}
				}
				out.println(builder.toString());
			} else {
				out.println("invalid");
			}

		} catch (Exception e) {

			out.println(e);
		}
		return null;
	}

	/*** Trap Exceptions during the upload and show errors back in view form ***/
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (exception instanceof MaxUploadSizeExceededException) {
			map.put("errors", exception.getMessage());
		} else {
			map.put("errors", "Unexpected error: " + exception.getMessage());
		}

		return new ModelAndView("home", map);
	}

}
