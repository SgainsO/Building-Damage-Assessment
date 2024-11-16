import os
from ultralytics import YOLO
import torch


print(torch.cuda.is_available())

# Define paths
dataset_path = '/content'
train_images = os.path.join(dataset_path, 'train/pics')
train_labels = os.path.join(dataset_path, 'train/labels')
val_images = os.path.join(dataset_path, 'val/pics')
val_labels = os.path.join(dataset_path, 'val/labels')

# Define YAML file for your dataset
dataset_yaml = """
train: train/images
val: valid/images



nc: 6  # Number of classes (adjust according to your dataset)
names: 
    - Broken Window
    - Building
    - Damage
    - Damaged roof
    - Other
    - Roof
""".format(dataset_path, dataset_path)

# Save YAML file
yaml_path = 'dataset.yaml'
with open(yaml_path, 'w') as f:
    f.write(dataset_yaml)

model = YOLO("yolo11s.pt")  

# Train the model
model.train(data=yaml_path, epochs=160, imgsz=640, )

# Save the model
model.save('DesModel.pt')

