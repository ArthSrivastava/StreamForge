import React, { useState } from 'react';
import { UploadCloud } from 'lucide-react';

const Home = () => {
  const [file, setFile] = useState(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);

  const handleChange = (e) => {
    if (e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files[0]) {
      setFile(e.dataTransfer.files[0]);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    
    if (!file) {
      return;
    }
    
    setIsUploading(true);
    setUploadProgress(0);
    
    try {
      // Simulate progress
      const progressInterval = setInterval(() => {
        setUploadProgress(prev => {
          const newProgress = prev + 5;
          if (newProgress >= 95) {
            clearInterval(progressInterval);
            return 95;
          }
          return newProgress;
        });
      }, 300);
      
      // Fetch Signed URL from the server
      const response = await fetch("http://localhost:8080/api/v1/videos", {
        method: "POST",
        body: JSON.stringify({
          name: file.name,
          description: "Demo Video",
          contentType: file.type
        }),
        headers: {
          "Content-Type": "application/json",
        },
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      
      const data = await response.json();
      
      // Upload the file to the S3 bucket using the Signed URL
      const uploadResponse = await fetch(data.videoUploadUrl, {
        method: "PUT",
        body: file,
        headers: {
          "Content-Type": file.type,
        }
      });
      
      clearInterval(progressInterval);
      
      if (uploadResponse.ok) {
        setUploadProgress(100);
        setTimeout(() => {
          setFile(null);
          setIsUploading(false);
          setUploadProgress(0);
        }, 2000);
      } else {
        const text = await uploadResponse.text();
        console.error("Error details:", text);
        throw new Error("Upload failed");
      }
    } catch (error) {
      console.error("Error:", error);
      setIsUploading(false);
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return '';
    if (bytes < 1024) return bytes + ' bytes';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  return (
    <div className="flex flex-col justify-center items-center min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 p-6">
      <div className="w-full max-w-md bg-gray-800 rounded-lg shadow-xl overflow-hidden">
        <div className="p-6 bg-gray-700 border-b border-gray-600">
          <h1 className="text-2xl font-bold text-white">Upload Your Video</h1>
          <p className="text-gray-300 mt-1">Share your amazing content with the world</p>
        </div>

        <form onSubmit={handleUpload} className="p-6">
          <div 
            className={`border-2 border-dashed rounded-lg p-8 transition-all ${
              isDragging ? 'border-blue-500 bg-blue-500 bg-opacity-10' : 'border-gray-600 hover:border-gray-500'
            } ${file ? 'bg-gray-700' : ''}`}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
          >
            <div className="flex flex-col items-center justify-center space-y-4">
              {!file ? (
                <>
                  <UploadCloud className="h-16 w-16 text-gray-400" />
                  <p className="text-gray-300 text-center">
                    Drag and drop your video file here<br />or click to browse
                  </p>
                  <input 
                    type="file" 
                    onChange={handleChange} 
                    className="hidden" 
                    id="file-upload" 
                    accept="video/*"
                  />
                  <label 
                    htmlFor="file-upload" 
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded-md text-white font-medium cursor-pointer transition-colors"
                  >
                    Select File
                  </label>
                </>
              ) : (
                <div className="w-full">
                  <div className="flex items-center mb-2">
                    <div className="flex-1 overflow-hidden">
                      <p className="text-white font-medium truncate">{file.name}</p>
                      <p className="text-gray-400 text-sm">{formatFileSize(file.size)}</p>
                    </div>
                    <button 
                      type="button"
                      onClick={() => setFile(null)}
                      className="ml-2 text-gray-400 hover:text-gray-200"
                    >
                      ✕
                    </button>
                  </div>
                  
                  {isUploading && (
                    <div className="mt-2">
                      <div className="h-2 w-full bg-gray-600 rounded-full overflow-hidden">
                        <div 
                          className="h-full bg-blue-500 transition-all duration-300 ease-out"
                          style={{ width: `${uploadProgress}%` }}
                        ></div>
                      </div>
                      <p className="text-gray-400 text-right text-sm mt-1">{uploadProgress}%</p>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>

          <div className="mt-6 flex justify-center">
            <button
              type="submit"
              disabled={!file || isUploading}
              className={`px-6 py-3 rounded-md text-white font-medium transition-colors ${
                !file || isUploading 
                  ? 'bg-gray-600 cursor-not-allowed' 
                  : 'bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-700 hover:to-blue-700'
              }`}
            >
              {isUploading ? 'Uploading...' : 'Upload Video'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Home;