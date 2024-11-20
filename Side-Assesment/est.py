from ultralytics import YOLO
import torch
from damage_assessment_yolo import *
from PIL import Image

class SideModel:
    def __init__(self, model_path):
        self.model = YOLO(model_path)
        self.estimator = BuildingDamageEstimator()
        self.damage_types = {
        0: 'Broken Window',
        1: 'Building',
        2: 'Damage',
        3: 'Damaged roof',
        4: 'Other',
        5: 'Roof'
    }
        
    def predict(self, image):
        return self.model(image)

    def testModel(self, image):
        result = self.predict(image)
        for i, r in enumerate(result):
        # Plot results image
            im_bgr = r.plot()  # BGR-order numpy array
            im_rgb = Image.fromarray(im_bgr[..., ::-1])  # RGB-order PIL image

            # Show results to screen (in supported environments)
            r.show()

            # Save results to disk
            r.save(filename=f"results{i}.jpg")


    """This model give the percent of the building that has visual damage.
        -1 means no buildings have been found and 2 means that there are multiple buildings"""
    def findPercentDestroyed(self, image):
        results = self.predict(image)
        if len(results) == 0:
            return 'inconclusive'
        else:
            return self.findDamageWithinBuilding(self.model(image, verbose=False))

        
    def getArea(self, xyxy):
        width = xyxy[2] - xyxy[0]
        height = xyxy[3] - xyxy[1]
        return width * height
    
    def withinChords(self, buildingChord, damageChord): ### 
        area_a = (damageChord[2] - damageChord[0]) * (damageChord[3] - damageChord[1])
        
        # Calculate the intersection coordinates
        x_min = max(damageChord[0], buildingChord[0])
        y_min = max(damageChord[1], buildingChord[1])
        x_max = min(damageChord[2], buildingChord[2])
        y_max = min(damageChord[3], buildingChord[3])
        
        # Calculate the intersection area
        if x_min < x_max and y_min < y_max:  # Ensure intersection exists
            intersection_area = (x_max - x_min) * (y_max - y_min)
        else:
            print("did not exist")
            intersection_area = 0

    #    print(intersection_area)        
        return intersection_area 
    
    def isSafeValue(self, val):     ##Both These Values are Undamged
        if val == 1 or val == 5 or val == 4:
            return True
        return False

    def findDamageWithinBuilding(self, results):
        damageTypes = []
        buildingArea = 0
        buildingChords = []

        foundBuilding = False
        foundRoof = False
        
        for r in results:  # Iterate through the results
            for box in r.boxes:  # Iterate through each box's class
                cls_value = box.cls.int()
                if cls_value == 1 and foundBuilding:  # Detected multiple buildings
                    print("multiple buildings found")
                    return -1

                if cls_value == 5 and foundRoof:  # First instance of a building
                    print("multiple roofs found")
                    return -1 
            
                if self.isSafeValue(cls_value) and not foundBuilding:  # First instance of a building
                    buildingChords = box.xyxy.tolist()[0]  # Select the coordinates for the current box
                    buildingArea = self.getArea(buildingChords)

                    if cls_value == 1:
                        foundBuilding = True
                    if cls_value == 5:
                        foundRoof = True

        areaAfterDamage = buildingArea 
        for r in results:                                               # Selects all the holes within said building file
            for box in r.boxes:
                label_id = box.cls.item()
                if not self.isSafeValue(label_id):
                    areaAfterDamage -= self.withinChords(buildingChords, box.xyxy.tolist()[0])
                    damageTypes.append(DamageAssessment(
                        damage_type=self.damage_types[label_id],
                        severity='unknown',
                        area= self.getArea(box.xyxy.tolist()[0]),
                        confidence=box.conf.int() 
                    ))
            
            percent = ((1 - (areaAfterDamage / buildingArea)) * 100)
            severity = self.estimator.classify_damage_severity(percent)
            
            for i in range(len(damageTypes)):
                damageTypes[i] = DamageAssessment(
                    damage_type=damageTypes[i].damage_type,
                    severity=severity,
                    area=damageTypes[i].area,
                    confidence=damageTypes[i].confidence  # confidence is not used in the current implementation
                )
        return [self.estimator.estimate_repair_cost(damageTypes), severity]
    def RunMultipleTests(self, imagesToTest):
        priceRange = [0,0]
        for image in imagesToTest:
            self.model(image)
            prices, sev = self.findPercentDestroyed(image)
            low, high= prices

            low = round(low, 2)
            high = round(high, 2)
 
            print(f"rebuilding {image}: "+ str(low) + " - " + str(high))
            print(f"The severity to rebuild this building would be {sev}")
            priceRange[0] += low
            priceRange[1] += high
        return priceRange


test = SideModel('best.pt')

test.testModel(['14.png', 'build.jpg', 'destroyed.jpg'])

#print("Range of the total cost to rebuild this area: " + str(test.RunMultipleTests(['14.png', 'build.jpg', 'destroyed.jpg'])))