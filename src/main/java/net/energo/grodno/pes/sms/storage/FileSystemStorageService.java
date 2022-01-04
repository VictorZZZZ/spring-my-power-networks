package net.energo.grodno.pes.sms.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

	private static final long MAX_FILE_SIZE = 52428800;
	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties, StorageProperties logLocation) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public String store(@NonNull MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory "
								+ filename);
			}
			if (file.getSize()>MAX_FILE_SIZE) {
				throw new StorageException("Слишком большой размер файла " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Date date = new Date();
				DateFormat df = new SimpleDateFormat("dd_MM_yy__HH_mm_ss_");
				Path newFile = this.rootLocation.resolve(df.format(date)+filename);

				if( Files.copy(inputStream, newFile,StandardCopyOption.REPLACE_EXISTING)>0 )
					return newFile.toAbsolutePath().toString();
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
		return null;
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public List<String> readFileToStringList(String filename){
		try {
			return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
		} catch (IOException e){
			throw new StorageException("Could not read file " + filename, e);
		}
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
